
package com.google.errorprone.refaster;

import java.util.List;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UAnnotation extends UAnnotation {

  private final UTree<?> annotationType;
  private final List<UExpression> arguments;

  AutoValue_UAnnotation(
      UTree<?> annotationType,
      List<UExpression> arguments) {
    if (annotationType == null) {
      throw new NullPointerException("Null annotationType");
    }
    this.annotationType = annotationType;
    if (arguments == null) {
      throw new NullPointerException("Null arguments");
    }
    this.arguments = arguments;
  }

  @Override
  public UTree<?> getAnnotationType() {
    return annotationType;
  }

  @Override
  public List<UExpression> getArguments() {
    return arguments;
  }

  @Override
  public String toString() {
    return "UAnnotation{"
         + "annotationType=" + annotationType + ", "
         + "arguments=" + arguments
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UAnnotation) {
      UAnnotation that = (UAnnotation) o;
      return (this.annotationType.equals(that.getAnnotationType()))
           && (this.arguments.equals(that.getArguments()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.annotationType.hashCode();
    h *= 1000003;
    h ^= this.arguments.hashCode();
    return h;
  }

}
