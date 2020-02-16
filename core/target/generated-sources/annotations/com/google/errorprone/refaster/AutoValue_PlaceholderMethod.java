
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.matchers.Matcher;
import com.sun.source.tree.ExpressionTree;
import java.lang.annotation.Annotation;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_PlaceholderMethod extends PlaceholderMethod {

  private final StringName name;
  private final UType returnType;
  private final ImmutableMap<UVariableDecl, ImmutableClassToInstanceMap<Annotation>> annotatedParameters;
  private final Matcher<ExpressionTree> matcher;
  private final ImmutableClassToInstanceMap<Annotation> annotations;

  AutoValue_PlaceholderMethod(
      StringName name,
      UType returnType,
      ImmutableMap<UVariableDecl, ImmutableClassToInstanceMap<Annotation>> annotatedParameters,
      Matcher<ExpressionTree> matcher,
      ImmutableClassToInstanceMap<Annotation> annotations) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    if (returnType == null) {
      throw new NullPointerException("Null returnType");
    }
    this.returnType = returnType;
    if (annotatedParameters == null) {
      throw new NullPointerException("Null annotatedParameters");
    }
    this.annotatedParameters = annotatedParameters;
    if (matcher == null) {
      throw new NullPointerException("Null matcher");
    }
    this.matcher = matcher;
    if (annotations == null) {
      throw new NullPointerException("Null annotations");
    }
    this.annotations = annotations;
  }

  @Override
  StringName name() {
    return name;
  }

  @Override
  UType returnType() {
    return returnType;
  }

  @Override
  ImmutableMap<UVariableDecl, ImmutableClassToInstanceMap<Annotation>> annotatedParameters() {
    return annotatedParameters;
  }

  @Override
  Matcher<ExpressionTree> matcher() {
    return matcher;
  }

  @Override
  ImmutableClassToInstanceMap<Annotation> annotations() {
    return annotations;
  }

  @Override
  public String toString() {
    return "PlaceholderMethod{"
         + "name=" + name + ", "
         + "returnType=" + returnType + ", "
         + "annotatedParameters=" + annotatedParameters + ", "
         + "matcher=" + matcher + ", "
         + "annotations=" + annotations
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof PlaceholderMethod) {
      PlaceholderMethod that = (PlaceholderMethod) o;
      return (this.name.equals(that.name()))
           && (this.returnType.equals(that.returnType()))
           && (this.annotatedParameters.equals(that.annotatedParameters()))
           && (this.matcher.equals(that.matcher()))
           && (this.annotations.equals(that.annotations()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.name.hashCode();
    h *= 1000003;
    h ^= this.returnType.hashCode();
    h *= 1000003;
    h ^= this.annotatedParameters.hashCode();
    h *= 1000003;
    h ^= this.matcher.hashCode();
    h *= 1000003;
    h ^= this.annotations.hashCode();
    return h;
  }

}
