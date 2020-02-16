
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UModifiers extends UModifiers {

  private final long flagBits;
  private final ImmutableList<UAnnotation> getAnnotations;

  AutoValue_UModifiers(
      long flagBits,
      ImmutableList<UAnnotation> getAnnotations) {
    this.flagBits = flagBits;
    if (getAnnotations == null) {
      throw new NullPointerException("Null getAnnotations");
    }
    this.getAnnotations = getAnnotations;
  }

  @Override
  long flagBits() {
    return flagBits;
  }

  @Override
  public ImmutableList<UAnnotation> getAnnotations() {
    return getAnnotations;
  }

  @Override
  public String toString() {
    return "UModifiers{"
         + "flagBits=" + flagBits + ", "
         + "getAnnotations=" + getAnnotations
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UModifiers) {
      UModifiers that = (UModifiers) o;
      return (this.flagBits == that.flagBits())
           && (this.getAnnotations.equals(that.getAnnotations()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (int) ((this.flagBits >>> 32) ^ this.flagBits);
    h *= 1000003;
    h ^= this.getAnnotations.hashCode();
    return h;
  }

}
