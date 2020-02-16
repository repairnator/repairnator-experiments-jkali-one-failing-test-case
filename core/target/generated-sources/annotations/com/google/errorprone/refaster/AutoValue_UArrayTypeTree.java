
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UArrayTypeTree extends UArrayTypeTree {

  private final UExpression type;

  AutoValue_UArrayTypeTree(
      UExpression type) {
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
  }

  @Override
  public UExpression getType() {
    return type;
  }

  @Override
  public String toString() {
    return "UArrayTypeTree{"
         + "type=" + type
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UArrayTypeTree) {
      UArrayTypeTree that = (UArrayTypeTree) o;
      return (this.type.equals(that.getType()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.type.hashCode();
    return h;
  }

}
