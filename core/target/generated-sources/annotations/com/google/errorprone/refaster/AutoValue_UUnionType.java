
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UUnionType extends UUnionType {

  private final ImmutableList<UExpression> typeAlternatives;

  AutoValue_UUnionType(
      ImmutableList<UExpression> typeAlternatives) {
    if (typeAlternatives == null) {
      throw new NullPointerException("Null typeAlternatives");
    }
    this.typeAlternatives = typeAlternatives;
  }

  @Override
  public ImmutableList<UExpression> getTypeAlternatives() {
    return typeAlternatives;
  }

  @Override
  public String toString() {
    return "UUnionType{"
         + "typeAlternatives=" + typeAlternatives
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UUnionType) {
      UUnionType that = (UUnionType) o;
      return (this.typeAlternatives.equals(that.getTypeAlternatives()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.typeAlternatives.hashCode();
    return h;
  }

}
