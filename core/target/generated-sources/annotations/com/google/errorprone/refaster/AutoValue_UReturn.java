
package com.google.errorprone.refaster;

import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UReturn extends UReturn {

  private final UExpression expression;

  AutoValue_UReturn(
      @Nullable UExpression expression) {
    this.expression = expression;
  }

  @Nullable
  @Override
  public UExpression getExpression() {
    return expression;
  }

  @Override
  public String toString() {
    return "UReturn{"
         + "expression=" + expression
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UReturn) {
      UReturn that = (UReturn) o;
      return ((this.expression == null) ? (that.getExpression() == null) : this.expression.equals(that.getExpression()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (expression == null) ? 0 : this.expression.hashCode();
    return h;
  }

}
