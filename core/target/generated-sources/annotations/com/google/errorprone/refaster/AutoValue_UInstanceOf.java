
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UInstanceOf extends UInstanceOf {

  private final UExpression expression;
  private final UTree<?> type;

  AutoValue_UInstanceOf(
      UExpression expression,
      UTree<?> type) {
    if (expression == null) {
      throw new NullPointerException("Null expression");
    }
    this.expression = expression;
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
  }

  @Override
  public UExpression getExpression() {
    return expression;
  }

  @Override
  public UTree<?> getType() {
    return type;
  }

  @Override
  public String toString() {
    return "UInstanceOf{"
         + "expression=" + expression + ", "
         + "type=" + type
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UInstanceOf) {
      UInstanceOf that = (UInstanceOf) o;
      return (this.expression.equals(that.getExpression()))
           && (this.type.equals(that.getType()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.expression.hashCode();
    h *= 1000003;
    h ^= this.type.hashCode();
    return h;
  }

}
