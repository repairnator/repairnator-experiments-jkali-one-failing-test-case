
package com.google.errorprone.refaster;

import java.util.List;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UForAll extends UForAll {

  private final List<UTypeVar> typeVars;
  private final UType quantifiedType;

  AutoValue_UForAll(
      List<UTypeVar> typeVars,
      UType quantifiedType) {
    if (typeVars == null) {
      throw new NullPointerException("Null typeVars");
    }
    this.typeVars = typeVars;
    if (quantifiedType == null) {
      throw new NullPointerException("Null quantifiedType");
    }
    this.quantifiedType = quantifiedType;
  }

  @Override
  public List<UTypeVar> getTypeVars() {
    return typeVars;
  }

  @Override
  public UType getQuantifiedType() {
    return quantifiedType;
  }

  @Override
  public String toString() {
    return "UForAll{"
         + "typeVars=" + typeVars + ", "
         + "quantifiedType=" + quantifiedType
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UForAll) {
      UForAll that = (UForAll) o;
      return (this.typeVars.equals(that.getTypeVars()))
           && (this.quantifiedType.equals(that.getQuantifiedType()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.typeVars.hashCode();
    h *= 1000003;
    h ^= this.quantifiedType.hashCode();
    return h;
  }

}
