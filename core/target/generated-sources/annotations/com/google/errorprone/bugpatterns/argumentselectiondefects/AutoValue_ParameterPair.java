
package com.google.errorprone.bugpatterns.argumentselectiondefects;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ParameterPair extends ParameterPair {

  private final Parameter formal;
  private final Parameter actual;

  AutoValue_ParameterPair(
      Parameter formal,
      Parameter actual) {
    if (formal == null) {
      throw new NullPointerException("Null formal");
    }
    this.formal = formal;
    if (actual == null) {
      throw new NullPointerException("Null actual");
    }
    this.actual = actual;
  }

  @Override
  Parameter formal() {
    return formal;
  }

  @Override
  Parameter actual() {
    return actual;
  }

  @Override
  public String toString() {
    return "ParameterPair{"
         + "formal=" + formal + ", "
         + "actual=" + actual
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ParameterPair) {
      ParameterPair that = (ParameterPair) o;
      return (this.formal.equals(that.formal()))
           && (this.actual.equals(that.actual()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.formal.hashCode();
    h *= 1000003;
    h ^= this.actual.hashCode();
    return h;
  }

}
