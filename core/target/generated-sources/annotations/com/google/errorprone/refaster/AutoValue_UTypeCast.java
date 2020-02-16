
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UTypeCast extends UTypeCast {

  private final UTree<?> type;
  private final UExpression expression;

  AutoValue_UTypeCast(
      UTree<?> type,
      UExpression expression) {
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
    if (expression == null) {
      throw new NullPointerException("Null expression");
    }
    this.expression = expression;
  }

  @Override
  public UTree<?> getType() {
    return type;
  }

  @Override
  public UExpression getExpression() {
    return expression;
  }

  @Override
  public String toString() {
    return "UTypeCast{"
         + "type=" + type + ", "
         + "expression=" + expression
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UTypeCast) {
      UTypeCast that = (UTypeCast) o;
      return (this.type.equals(that.getType()))
           && (this.expression.equals(that.getExpression()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.type.hashCode();
    h *= 1000003;
    h ^= this.expression.hashCode();
    return h;
  }

}
