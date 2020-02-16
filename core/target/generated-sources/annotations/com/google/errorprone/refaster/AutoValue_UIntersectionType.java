
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UIntersectionType extends UIntersectionType {

  private final ImmutableList<UExpression> bounds;

  AutoValue_UIntersectionType(
      ImmutableList<UExpression> bounds) {
    if (bounds == null) {
      throw new NullPointerException("Null bounds");
    }
    this.bounds = bounds;
  }

  @Override
  public ImmutableList<UExpression> getBounds() {
    return bounds;
  }

  @Override
  public String toString() {
    return "UIntersectionType{"
         + "bounds=" + bounds
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UIntersectionType) {
      UIntersectionType that = (UIntersectionType) o;
      return (this.bounds.equals(that.getBounds()));
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
