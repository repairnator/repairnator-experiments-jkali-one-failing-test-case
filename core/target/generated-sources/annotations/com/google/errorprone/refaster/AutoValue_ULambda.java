
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableList;
import com.sun.tools.javac.tree.JCTree;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ULambda extends ULambda {

  private final JCTree.JCLambda.ParameterKind parameterKind;
  private final ImmutableList<UVariableDecl> getParameters;
  private final UTree<?> getBody;

  AutoValue_ULambda(
      JCTree.JCLambda.ParameterKind parameterKind,
      ImmutableList<UVariableDecl> getParameters,
      UTree<?> getBody) {
    if (parameterKind == null) {
      throw new NullPointerException("Null parameterKind");
    }
    this.parameterKind = parameterKind;
    if (getParameters == null) {
      throw new NullPointerException("Null getParameters");
    }
    this.getParameters = getParameters;
    if (getBody == null) {
      throw new NullPointerException("Null getBody");
    }
    this.getBody = getBody;
  }

  @Override
  JCTree.JCLambda.ParameterKind parameterKind() {
    return parameterKind;
  }

  @Override
  public ImmutableList<UVariableDecl> getParameters() {
    return getParameters;
  }

  @Override
  public UTree<?> getBody() {
    return getBody;
  }

  @Override
  public String toString() {
    return "ULambda{"
         + "parameterKind=" + parameterKind + ", "
         + "getParameters=" + getParameters + ", "
         + "getBody=" + getBody
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ULambda) {
      ULambda that = (ULambda) o;
      return (this.parameterKind.equals(that.parameterKind()))
           && (this.getParameters.equals(that.getParameters()))
           && (this.getBody.equals(that.getBody()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.parameterKind.hashCode();
    h *= 1000003;
    h ^= this.getParameters.hashCode();
    h *= 1000003;
    h ^= this.getBody.hashCode();
    return h;
  }

}
