
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UThrow extends UThrow {

  private final UExpression expression;

  AutoValue_UThrow(
      UExpression expression) {
    if (expression == null) {
      throw new NullPointerException("Null expression");
    }
    this.expression = expression;
  }

  @Override
  public UExpression getExpression() {
    return expression;
  }

  @Override
  public String toString() {
    return "UThrow{"
         + "expression=" + expression
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UThrow) {
      UThrow that = (UThrow) o;
      return (this.expression.equals(that.getExpression()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.expression.hashCode();
    return h;
  }

}
