
package com.google.errorprone.bugpatterns;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_IsInstanceOfClass_Operand extends IsInstanceOfClass.Operand {

  private final IsInstanceOfClass.Kind kind;
  private final CharSequence value;
  private final CharSequence source;

  AutoValue_IsInstanceOfClass_Operand(
      IsInstanceOfClass.Kind kind,
      CharSequence value,
      CharSequence source) {
    if (kind == null) {
      throw new NullPointerException("Null kind");
    }
    this.kind = kind;
    if (value == null) {
      throw new NullPointerException("Null value");
    }
    this.value = value;
    if (source == null) {
      throw new NullPointerException("Null source");
    }
    this.source = source;
  }

  @Override
  IsInstanceOfClass.Kind kind() {
    return kind;
  }

  @Override
  CharSequence value() {
    return value;
  }

  @Override
  CharSequence source() {
    return source;
  }

  @Override
  public String toString() {
    return "Operand{"
         + "kind=" + kind + ", "
         + "value=" + value + ", "
         + "source=" + source
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof IsInstanceOfClass.Operand) {
      IsInstanceOfClass.Operand that = (IsInstanceOfClass.Operand) o;
      return (this.kind.equals(that.kind()))
           && (this.value.equals(that.value()))
           && (this.source.equals(that.source()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.kind.hashCode();
    h *= 1000003;
    h ^= this.value.hashCode();
    h *= 1000003;
    h ^= this.source.hashCode();
    return h;
  }

}
