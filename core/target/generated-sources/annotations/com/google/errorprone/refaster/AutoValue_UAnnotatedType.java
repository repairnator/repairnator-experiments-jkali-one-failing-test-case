
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UAnnotatedType extends UAnnotatedType {

  private final ImmutableList<UAnnotation> annotations;
  private final UExpression underlyingType;

  AutoValue_UAnnotatedType(
      ImmutableList<UAnnotation> annotations,
      UExpression underlyingType) {
    if (annotations == null) {
      throw new NullPointerException("Null annotations");
    }
    this.annotations = annotations;
    if (underlyingType == null) {
      throw new NullPointerException("Null underlyingType");
    }
    this.underlyingType = underlyingType;
  }

  @Override
  public ImmutableList<UAnnotation> getAnnotations() {
    return annotations;
  }

  @Override
  public UExpression getUnderlyingType() {
    return underlyingType;
  }

  @Override
  public String toString() {
    return "UAnnotatedType{"
         + "annotations=" + annotations + ", "
         + "underlyingType=" + underlyingType
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UAnnotatedType) {
      UAnnotatedType that = (UAnnotatedType) o;
      return (this.annotations.equals(that.getAnnotations()))
           && (this.underlyingType.equals(that.getUnderlyingType()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.annotations.hashCode();
    h *= 1000003;
    h ^= this.underlyingType.hashCode();
    return h;
  }

}
