
package com.google.errorprone.bugpatterns;

import com.sun.tools.javac.code.Type;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_EqualsIncompatibleType_TypeCompatibilityReport extends EqualsIncompatibleType.TypeCompatibilityReport {

  private final boolean compatible;
  private final Type lhs;
  private final Type rhs;

  AutoValue_EqualsIncompatibleType_TypeCompatibilityReport(
      boolean compatible,
      @Nullable Type lhs,
      @Nullable Type rhs) {
    this.compatible = compatible;
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public boolean compatible() {
    return compatible;
  }

  @Nullable
  @Override
  public Type lhs() {
    return lhs;
  }

  @Nullable
  @Override
  public Type rhs() {
    return rhs;
  }

  @Override
  public String toString() {
    return "TypeCompatibilityReport{"
         + "compatible=" + compatible + ", "
         + "lhs=" + lhs + ", "
         + "rhs=" + rhs
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof EqualsIncompatibleType.TypeCompatibilityReport) {
      EqualsIncompatibleType.TypeCompatibilityReport that = (EqualsIncompatibleType.TypeCompatibilityReport) o;
      return (this.compatible == that.compatible())
           && ((this.lhs == null) ? (that.lhs() == null) : this.lhs.equals(that.lhs()))
           && ((this.rhs == null) ? (that.rhs() == null) : this.rhs.equals(that.rhs()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.compatible ? 1231 : 1237;
    h *= 1000003;
    h ^= (lhs == null) ? 0 : this.lhs.hashCode();
    h *= 1000003;
    h ^= (rhs == null) ? 0 : this.rhs.hashCode();
    return h;
  }

}
