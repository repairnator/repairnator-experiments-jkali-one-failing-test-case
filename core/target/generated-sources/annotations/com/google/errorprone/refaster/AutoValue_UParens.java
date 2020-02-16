
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UParens extends UParens {

  private final UExpression expression;

  AutoValue_UParens(
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
    return "UParens{"
         + "expression=" + expression
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UParens) {
      UParens that = (UParens) o;
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
