
package com.google.errorprone.refaster;

import com.sun.tools.javac.code.BoundKind;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UWildcardType extends UWildcardType {

  private final BoundKind boundKind;
  private final UType bound;

  AutoValue_UWildcardType(
      BoundKind boundKind,
      UType bound) {
    if (boundKind == null) {
      throw new NullPointerException("Null boundKind");
    }
    this.boundKind = boundKind;
    if (bound == null) {
      throw new NullPointerException("Null bound");
    }
    this.bound = bound;
  }

  @Override
  BoundKind boundKind() {
    return boundKind;
  }

  @Override
  UType bound() {
    return bound;
  }

  @Override
  public String toString() {
    return "UWildcardType{"
         + "boundKind=" + boundKind + ", "
         + "bound=" + bound
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UWildcardType) {
      UWildcardType that = (UWildcardType) o;
      return (this.boundKind.equals(that.boundKind()))
           && (this.bound.equals(that.bound()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.boundKind.hashCode();
    h *= 1000003;
    h ^= this.bound.hashCode();
    return h;
  }

}
