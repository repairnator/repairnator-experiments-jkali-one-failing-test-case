
package com.google.errorprone.bugpatterns.apidiff;

import com.google.common.collect.ImmutableList;
import java.nio.file.Path;
import javax.annotation.processing.Generated;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_CompilationBuilderHelpers_CompilationResult extends CompilationBuilderHelpers.CompilationResult {

  private final boolean ok;
  private final ImmutableList<Diagnostic<? extends JavaFileObject>> diagnostics;
  private final String errorOutput;
  private final Path classOutput;

  AutoValue_CompilationBuilderHelpers_CompilationResult(
      boolean ok,
      ImmutableList<Diagnostic<? extends JavaFileObject>> diagnostics,
      String errorOutput,
      Path classOutput) {
    this.ok = ok;
    if (diagnostics == null) {
      throw new NullPointerException("Null diagnostics");
    }
    this.diagnostics = diagnostics;
    if (errorOutput == null) {
      throw new NullPointerException("Null errorOutput");
    }
    this.errorOutput = errorOutput;
    if (classOutput == null) {
      throw new NullPointerException("Null classOutput");
    }
    this.classOutput = classOutput;
  }

  @Override
  boolean ok() {
    return ok;
  }

  @Override
  ImmutableList<Diagnostic<? extends JavaFileObject>> diagnostics() {
    return diagnostics;
  }

  @Override
  String errorOutput() {
    return errorOutput;
  }

  @Override
  Path classOutput() {
    return classOutput;
  }

  @Override
  public String toString() {
    return "CompilationResult{"
         + "ok=" + ok + ", "
         + "diagnostics=" + diagnostics + ", "
         + "errorOutput=" + errorOutput + ", "
         + "classOutput=" + classOutput
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof CompilationBuilderHelpers.CompilationResult) {
      CompilationBuilderHelpers.CompilationResult that = (CompilationBuilderHelpers.CompilationResult) o;
      return (this.ok == that.ok())
           && (this.diagnostics.equals(that.diagnostics()))
           && (this.errorOutput.equals(that.errorOutput()))
           && (this.classOutput.equals(that.classOutput()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.ok ? 1231 : 1237;
    h *= 1000003;
    h ^= this.diagnostics.hashCode();
    h *= 1000003;
    h ^= this.errorOutput.hashCode();
    h *= 1000003;
    h ^= this.classOutput.hashCode();
    return h;
  }

}
