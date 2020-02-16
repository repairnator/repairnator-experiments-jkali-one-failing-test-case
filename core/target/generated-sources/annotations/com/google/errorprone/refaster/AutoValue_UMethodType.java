
package com.google.errorprone.refaster;

import java.util.List;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UMethodType extends UMethodType {

  private final UType returnType;
  private final List<UType> parameterTypes;

  AutoValue_UMethodType(
      UType returnType,
      List<UType> parameterTypes) {
    if (returnType == null) {
      throw new NullPointerException("Null returnType");
    }
    this.returnType = returnType;
    if (parameterTypes == null) {
      throw new NullPointerException("Null parameterTypes");
    }
    this.parameterTypes = parameterTypes;
  }

  @Override
  public UType getReturnType() {
    return returnType;
  }

  @Override
  public List<UType> getParameterTypes() {
    return parameterTypes;
  }

  @Override
  public String toString() {
    return "UMethodType{"
         + "returnType=" + returnType + ", "
         + "parameterTypes=" + parameterTypes
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UMethodType) {
      UMethodType that = (UMethodType) o;
      return (this.returnType.equals(that.getReturnType()))
           && (this.parameterTypes.equals(that.getParameterTypes()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.returnType.hashCode();
    h *= 1000003;
    h ^= this.parameterTypes.hashCode();
    return h;
  }

}
