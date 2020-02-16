
package com.google.errorprone.refaster;

import java.util.List;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UClassType extends UClassType {

  private final StringName fullyQualifiedClass;
  private final List<UType> typeArguments;

  AutoValue_UClassType(
      StringName fullyQualifiedClass,
      List<UType> typeArguments) {
    if (fullyQualifiedClass == null) {
      throw new NullPointerException("Null fullyQualifiedClass");
    }
    this.fullyQualifiedClass = fullyQualifiedClass;
    if (typeArguments == null) {
      throw new NullPointerException("Null typeArguments");
    }
    this.typeArguments = typeArguments;
  }

  @Override
  StringName fullyQualifiedClass() {
    return fullyQualifiedClass;
  }

  @Override
  List<UType> typeArguments() {
    return typeArguments;
  }

  @Override
  public String toString() {
    return "UClassType{"
         + "fullyQualifiedClass=" + fullyQualifiedClass + ", "
         + "typeArguments=" + typeArguments
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UClassType) {
      UClassType that = (UClassType) o;
      return (this.fullyQualifiedClass.equals(that.fullyQualifiedClass()))
           && (this.typeArguments.equals(that.typeArguments()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.fullyQualifiedClass.hashCode();
    h *= 1000003;
    h ^= this.typeArguments.hashCode();
    return h;
  }

}
