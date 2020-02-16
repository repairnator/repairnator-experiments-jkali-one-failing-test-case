/*
 * Copyright 2017 The Error Prone Authors.
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

package com.google.errorprone.bugpatterns;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.SeverityLevel.SUGGESTION;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Ascii;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import com.google.errorprone.BugPattern;
import com.google.errorprone.BugPattern.LinkType;
import com.google.errorprone.BugPattern.ProvidesFix;
import com.google.errorprone.BugPattern.StandardTags;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker.TypeParameterTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.names.NamingConventions;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.TypeVariableSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Name;

/**
 * Enforces type parameters match the google style guide.
 *
 * @author siyuanl@google.com (Siyuan Liu)
 * @author glorioso@google.com (Nick Glorioso)
 */
@BugPattern(
    name = "TypeParameterNaming",
    summary =
        "Type parameters must be a single letter with an optional numeric suffix,"
            + " or an UpperCamelCase name followed by the letter 'T'.",
    category = JDK,
    severity = SUGGESTION,
    tags = StandardTags.STYLE,
    linkType = LinkType.CUSTOM,
    providesFix = ProvidesFix.REQUIRES_HUMAN_ATTENTION,
    link = "https://google.github.io/styleguide/javaguide.html#s5.2.8-type-variable-names"
    )
public class TypeParameterNaming extends BugChecker implements TypeParameterTreeMatcher {
  private static final Pattern SINGLE_PLUS_MAYBE_DIGIT = Pattern.compile("[A-Z]\\d?");
  private static final Pattern TRAILING_DIGIT_EXTRACTOR = Pattern.compile("^(.*?)(\\d+)$");

  private static String upperCamelToken(String s) {
    return "" + Ascii.toUpperCase(s.charAt(0)) + (s.length() == 1 ? "" : s.substring(1));
  }

  @Override
  public Description matchTypeParameter(TypeParameterTree tree, VisitorState state) {

    if (matchesTypeParameterNamingScheme(tree.getName())) {
      return Description.NO_MATCH;
    }

    return buildDescription(tree)
        .setMessage(
            String.format(
                "Type Parameter %s must be a single letter with an optional numeric"
                    + " suffix, or an UpperCamelCase name followed by the letter 'T'.",
                tree.getName()))
        .addFix(
            TypeParameterShadowing.renameTypeVariable(
                tree,
                state.getPath().getParentPath().getLeaf(),
                suggestedNameFollowedWithT(tree.getName().toString()),
                state))
        .addFix(
            TypeParameterShadowing.renameTypeVariable(
                tree,
                state.getPath().getParentPath().getLeaf(),
                suggestedSingleLetter(tree.getName().toString(), tree),
                state))
        .build();
  }

  // Get list of type params of every enclosing class
  private static List<TypeVariableSymbol> typeVariablesEnclosing(Symbol sym) {
    List<TypeVariableSymbol> typeVarScopes = new ArrayList<>();
    outer:
    while (!sym.isStatic()) {
      sym = sym.owner;
      switch (sym.getKind()) {
        case PACKAGE:
          break outer;
        case METHOD:
        case CLASS:
          typeVarScopes.addAll(0, sym.getTypeParameters());
          break;
        default: // fall out
      }
    }
    return typeVarScopes;
  }

  private static String suggestedSingleLetter(String id, Tree tree) {
    char firstLetter = id.charAt(0);
    Symbol sym = ASTHelpers.getSymbol(tree);
    List<TypeVariableSymbol> enclosingTypeSymbols = typeVariablesEnclosing(sym);

    for (TypeVariableSymbol typeName : enclosingTypeSymbols) {
      char enclosingTypeFirstLetter = typeName.toString().charAt(0);
      if (enclosingTypeFirstLetter == firstLetter
          && !matchesTypeParameterNamingScheme(typeName.name)) {
        List<String> typeVarsInScope =
            Streams.concat(enclosingTypeSymbols.stream(), sym.getTypeParameters().stream())
                .map(v -> v.name.toString())
                .collect(toImmutableList());

        return firstLetterReplacementName(id, typeVarsInScope);
      }
    }

    return Character.toString(firstLetter);
  }
  // T -> T2
  // T2 -> T3
  // T -> T4 (if T2 and T3 already exist)
  // TODO(siyuanl) : combine this method with TypeParameterShadowing.replacementTypeVarName
  private static String firstLetterReplacementName(String name, List<String> superTypeVars) {
    String firstLetterOfBase = Character.toString(name.charAt(0));
    int typeVarNum = 2;
    boolean first = true;

    Matcher matcher = TRAILING_DIGIT_EXTRACTOR.matcher(name);
    if (matcher.matches()) {
      name = matcher.group(1);
      typeVarNum = Integer.parseInt(matcher.group(2)) + 1;
    }

    String replacementName = "";

    // Look at the type names to the left of the current type
    // Since this bugchecker doesn't rename as it goes, we have to check which type names
    // would've been renamed before the current ones
    for (String superTypeVar : superTypeVars) {
      if (superTypeVar.equals(name)) {
        if (typeVarNum == 2 && first) {
          return firstLetterOfBase;
        }
        break;
      } else if (superTypeVar.charAt(0) == name.charAt(0)) {
        if (!first) {
          typeVarNum++;
        } else {
          first = false;
        }
        replacementName = firstLetterOfBase + typeVarNum;
      }
    }

    while (superTypeVars.contains(replacementName)) {
      typeVarNum++;
      replacementName = firstLetterOfBase + typeVarNum;
    }

    return replacementName;
  }

  static boolean matchesTypeParameterNamingScheme(Name name) {
    return SINGLE_PLUS_MAYBE_DIGIT.matcher(name).matches() || matchesClassWithT(name.toString());
  }

  private static String suggestedNameFollowedWithT(String identifier) {
    Preconditions.checkArgument(!identifier.isEmpty());

    // Some early checks:
    // TFoo => FooT
    if (identifier.length() > 2
        && identifier.charAt(0) == 'T'
        && Ascii.isUpperCase(identifier.charAt(1))
        && Ascii.isLowerCase(identifier.charAt(2))) {
      // splitToLowercaseTerms thinks "TFooBar" is ["tfoo", "bar"], so we remove "t", have it parse
      // as ["foo", "bar"], then staple "t" back on the end.
      ImmutableList<String> tokens =
          NamingConventions.splitToLowercaseTerms(identifier.substring(1));
      return Streams.concat(tokens.stream(), Stream.of("T"))
          .map(TypeParameterNaming::upperCamelToken)
          .collect(Collectors.joining());
    }

    ImmutableList<String> tokens = NamingConventions.splitToLowercaseTerms(identifier);

    // UPPERCASE => UppercaseT
    if (tokens.size() == 1) {
      String token = tokens.get(0);
      if (token.toUpperCase().equals(identifier)) {
        return upperCamelToken(token) + "T";
      }
    }

    // FooType => FooT
    if (Iterables.getLast(tokens).equals("type")) {
      return Streams.concat(tokens.subList(0, tokens.size() - 1).stream(), Stream.of("T"))
          .map(TypeParameterNaming::upperCamelToken)
          .collect(Collectors.joining());
    }

    return identifier + "T";
  }

  @VisibleForTesting
  static boolean matchesClassWithT(String identifier) {
    if (!identifier.endsWith("T")) {
      return false;
    }

    ImmutableList<String> tokens = NamingConventions.splitToLowercaseTerms(identifier);
    // Combine the tokens back into UpperCamelTokens and make sure it matches the identifier
    String reassembled =
        tokens.stream().map(TypeParameterNaming::upperCamelToken).collect(Collectors.joining());

    return identifier.equals(reassembled);
  }
}
