
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UIntersectionClassType extends UIntersectionClassType {

  private final ImmutableList<UType> bounds;

  AutoValue_UIntersectionClassType(
      ImmutableList<UType> bounds) {
    if (bounds == null) {
      throw new NullPointerException("Null bounds");
    }
    this.bounds = bounds;
  }

  @Override
  ImmutableList<UType> bounds() {
    return bounds;
  }

  @Override
  public String toString() {
    return "UIntersectionClassType{"
         + "bounds=" + bounds
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UIntersectionClassType) {
      UIntersectionClassType that = (UIntersectionClassType) o;
      return (this.bounds.equals(that.bounds()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.bounds.hashCode();
    return h;
  }

}
