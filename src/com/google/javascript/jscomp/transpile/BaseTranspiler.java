/*
 * Copyright 2016 The Closure Compiler Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.javascript.jscomp.transpile;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CompilerOptions.LanguageMode;
import com.google.javascript.jscomp.DiagnosticGroup;
import com.google.javascript.jscomp.DiagnosticType;
import com.google.javascript.jscomp.Es6RewriteModulesToCommonJsModules;
import com.google.javascript.jscomp.PropertyRenamingPolicy;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.SourceMap;
import com.google.javascript.jscomp.VariableRenamingPolicy;
import com.google.javascript.jscomp.bundle.TranspilationException;
import com.google.javascript.jscomp.deps.ModuleLoader;
import com.google.javascript.jscomp.deps.ModuleLoader.PathEscaper;
import com.google.javascript.jscomp.deps.ModuleLoader.ResolutionMode;
import com.google.javascript.rhino.Node;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Basic Transpiler implementation for outputting ES5 code.
 */
public final class BaseTranspiler implements Transpiler {

  private final CompilerSupplier compilerSupplier;
  private final String runtimeLibraryName;

  public BaseTranspiler(CompilerSupplier compilerSupplier, String runtimeLibraryName) {
    this.compilerSupplier = checkNotNull(compilerSupplier);
    this.runtimeLibraryName = checkNotNull(runtimeLibraryName);
  }

  @Override
  public TranspileResult transpile(URI path, String code) {
    CompileResult result = compilerSupplier.compile(path, code);
    if (!result.transpiled) {
      return new TranspileResult(path, code, code, "");
    }
    return new TranspileResult(path, code, result.source, result.sourceMap);
  }

  @Override
  public String runtime() {
    StringBuilder sb = new StringBuilder();
    if (!Strings.isNullOrEmpty(runtimeLibraryName)) {
      sb.append(compilerSupplier.runtime(runtimeLibraryName));
    }
    sb.append(compilerSupplier.runtime("modules"));
    return sb.toString();
  }

  public static final BaseTranspiler ES5_TRANSPILER = new BaseTranspiler(
      new CompilerSupplier(), "es6_runtime");

  public static final BaseTranspiler ES_MODULE_TO_CJS_TRANSPILER =
      new BaseTranspiler(new EsmToCjsCompilerSupplier(), "");

  /**
   * Wraps the Compiler into a more relevant interface, making it
   * easy to test the Transpiler without depending on implementation
   * details of the Compiler itself.  Also works around the fact
   * that the Compiler is not thread-safe (since we may do multiple
   * transpiles concurrently), so we supply a fresh instance each
   * time when we're in single-file mode.
   */
  public static class CompilerSupplier {
    protected final ResolutionMode moduleResolution;
    protected final ImmutableList<String> moduleRoots;
    protected final ImmutableMap<String, String> prefixReplacements;

    public CompilerSupplier() {
      // Use the default resolution mode
      this(new CompilerOptions().getModuleResolutionMode(), ImmutableList.of(), ImmutableMap.of());
    }

    /**
     * Accepts commonly overridden options for ES6 modules to avoid needed to subclass.
     *
     * @param moduleResolution module resolution for resolving import paths
     * @param prefixReplacements prefix replacements for when moduleResolution is {@link
     *     ModuleLoader.ResolutionMode#BROWSER_WITH_TRANSFORMED_PREFIXES}
     */
    public CompilerSupplier(
        ModuleLoader.ResolutionMode moduleResolution,
        ImmutableList<String> moduleRoots,
        ImmutableMap<String, String> prefixReplacements) {
      this.moduleResolution = moduleResolution;
      this.moduleRoots = moduleRoots;
      this.prefixReplacements = prefixReplacements;
    }

    public CompileResult compile(URI path, String code) {
      Compiler compiler = compiler();
      Result result =
          compiler.compile(EXTERNS, SourceFile.fromCode(path.toString(), code), options());
      String source = compiler.toSource();
      StringBuilder sourceMap = new StringBuilder();
      if (result.sourceMap != null) {
        try {
          result.sourceMap.appendTo(sourceMap, path.toString());
        } catch (IOException e) {
          // impossible, and not a big deal even if it did happen.
        }
      }
      boolean transpiled = !result.transpiledFiles.isEmpty();
      if (result.errors.length > 0) {
        throw new TranspilationException(compiler, result.errors, result.warnings);
      }
      return new CompileResult(
          source,
          transpiled,
          transpiled ? sourceMap.toString() : "");
    }

    public String runtime(String library) {
      Compiler compiler = compiler();
      CompilerOptions options = options();
      options.setForceLibraryInjection(ImmutableList.of(library));
      compiler.compile(EXTERNS, EMPTY, options);
      return compiler.toSource();
    }

    protected Compiler compiler() {
      return new Compiler();
    }

    protected CompilerOptions options() {
      CompilerOptions options = new CompilerOptions();
      setOptions(options);
      return options;
    }

    protected void setOptions(CompilerOptions options) {
      options.setLanguageIn(LanguageMode.ECMASCRIPT_NEXT);
      // TODO(sdh): It would be nice to allow people to output code in
      // strict mode.  But currently we swallow all the input language
      // strictness checks, and there are various tests that are never
      // compiled and so are broken when we output 'use strict'.  We
      // could consider adding some sort of logging/warning/error in
      // cases where the input was not strict, though there could still
      // be semantic differences even if syntax is strict.  Possibly
      // the first step would be to allow the option of outputting strict
      // and then change the default and see what breaks.  b/33005948
      options.setLanguageOut(LanguageMode.ECMASCRIPT5);
      options.setQuoteKeywordProperties(true);
      options.setSkipNonTranspilationPasses(true);
      options.setVariableRenaming(VariableRenamingPolicy.OFF);
      options.setPropertyRenaming(PropertyRenamingPolicy.OFF);
      options.setWrapGoogModulesForWhitespaceOnly(false);
      options.setPrettyPrint(true);
      options.setWarningLevel(ES5_WARNINGS, CheckLevel.OFF);
      options.setTranspileEs6ModulesToCjsModules(true);
      options.setModuleResolutionMode(moduleResolution);
      options.setModuleRoots(moduleRoots);
      options.setBrowserResolverPrefixReplacements(prefixReplacements);
      // Don't escape module paths when bundling in the event paths are URLs.
      options.setPathEscaper(PathEscaper.CANONICALIZE_ONLY);

      options.setSourceMapOutputPath("/dev/null");
      options.setSourceMapIncludeSourcesContent(true);
      // Make sourcemaps use absolute paths, so that the path is not duplicated if a build tool adds
      // a sourceurl. Exception: if the location has a scheme (like http:) then leave the path
      // intact. This makes this usable from web servers.
      options.setSourceMapLocationMappings(
          ImmutableList.of((location) -> {
            try {
              if (new URI(location).getScheme() != null) {
                return location;
              }
            } catch (URISyntaxException e) {
              // Swallow, return the absolute version below.
            }
            return new SourceMap.PrefixLocationMapping("", "/").map(location);
          }));
    }

    protected static final SourceFile EXTERNS =
        SourceFile.fromCode("externs.js", "function Symbol() {}");
    protected static final SourceFile EMPTY = SourceFile.fromCode("empty.js", "");
    protected static final DiagnosticGroup ES5_WARNINGS = new DiagnosticGroup(
        DiagnosticType.error("JSC_CANNOT_CONVERT", ""));
  }

  /**
   * CompilerSupplier that only transforms EcmaScript Modules into a form that can be safely
   * transformed on a file by file basis and concatenated.
   */
  public static class EsmToCjsCompilerSupplier extends CompilerSupplier {

    public EsmToCjsCompilerSupplier() {
      super();
    }

    public EsmToCjsCompilerSupplier(
        ModuleLoader.ResolutionMode moduleResolution,
        ImmutableList<String> moduleRoots,
        ImmutableMap<String, String> prefixReplacements) {
      super(moduleResolution, moduleRoots, prefixReplacements);
    }


    @Override
    public CompileResult compile(URI path, String code) {
      CompilerOptions options = new CompilerOptions();
      options.setLanguageIn(LanguageMode.ECMASCRIPT_NEXT);
      options.setEmitUseStrict(false);
      options.setSourceMapOutputPath("/dev/null");
      options.setSourceMapIncludeSourcesContent(true);
      options.setPrettyPrint(true);
      options.setModuleResolutionMode(moduleResolution);
      options.setModuleRoots(moduleRoots);
      options.setBrowserResolverPrefixReplacements(prefixReplacements);

      // Create a compiler and run specifically this one pass on it.
      Compiler compiler = compiler();
      compiler.init(
          ImmutableList.of(),
          ImmutableList.of(SourceFile.fromCode(path.toString(), code)),
          options);
      compiler.parseForCompilation();

      boolean transpiled = false;

      if (!compiler.hasErrors()
          && compiler.getRoot().getSecondChild().getFirstFirstChild().isModuleBody()
          && !compiler
              .getRoot()
              .getSecondChild()
              .getFirstChild()
              .getBooleanProp(Node.GOOG_MODULE)) {
        new Es6RewriteModulesToCommonJsModules(compiler)
            .process(null, compiler.getRoot().getSecondChild());
        compiler.getRoot().getSecondChild().getFirstChild().putBooleanProp(Node.TRANSPILED, true);
        transpiled = true;
      }

      Result result = compiler.getResult();
      String source = compiler.toSource();
      StringBuilder sourceMap = new StringBuilder();
      if (result.sourceMap != null) {
        try {
          result.sourceMap.appendTo(sourceMap, path.toString());
        } catch (IOException e) {
          // impossible, and not a big deal even if it did happen.
        }
      }
      if (result.errors.length > 0) {
        throw new TranspilationException(compiler, result.errors, result.warnings);
      }
      return new CompileResult(source, transpiled, transpiled ? sourceMap.toString() : "");
    }
  }

  /**
   * The source together with the additional compilation results.
   */
  public static class CompileResult {
    public final String source;
    public final boolean transpiled;
    public final String sourceMap;
    public CompileResult(String source, boolean transpiled, String sourceMap) {
      this.source = checkNotNull(source);
      this.transpiled = transpiled;
      this.sourceMap = checkNotNull(sourceMap);
    }
  }
}
