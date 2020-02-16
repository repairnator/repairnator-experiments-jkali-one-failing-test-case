
package com.google.errorprone.bugpatterns;

import com.sun.tools.javac.code.Symbol;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_EqualsWrongThing_ComparisonPair extends EqualsWrongThing.ComparisonPair {

  private final Symbol lhs;
  private final Symbol rhs;

  AutoValue_EqualsWrongThing_ComparisonPair(
      Symbol lhs,
      Symbol rhs) {
    if (lhs == null) {
      throw new NullPointerException("Null lhs");
    }
    this.lhs = lhs;
    if (rhs == null) {
      throw new NullPointerException("Null rhs");
    }
    this.rhs = rhs;
  }

  @Override
  Symbol lhs() {
    return lhs;
  }

  @Override
  Symbol rhs() {
    return rhs;
  }

  @Override
  public String toString() {
    return "ComparisonPair{"
         + "lhs=" + lhs + ", "
         + "rhs=" + rhs
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof EqualsWrongThing.ComparisonPair) {
      EqualsWrongThing.ComparisonPair that = (EqualsWrongThing.ComparisonPair) o;
      return (this.lhs.equals(that.lhs()))
           && (this.rhs.equals(that.rhs()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.lhs.hashCode();
    h *= 1000003;
    h ^= this.rhs.hashCode();
    return h;
  }

}
