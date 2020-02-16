
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UArrayType extends UArrayType {

  private final UType componentType;

  AutoValue_UArrayType(
      UType componentType) {
    if (componentType == null) {
      throw new NullPointerException("Null componentType");
    }
    this.componentType = componentType;
  }

  @Override
  UType componentType() {
    return componentType;
  }

  @Override
  public String toString() {
    return "UArrayType{"
         + "componentType=" + componentType
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UArrayType) {
      UArrayType that = (UArrayType) o;
      return (this.componentType.equals(that.componentType()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.componentType.hashCode();
    return h;
  }

}
