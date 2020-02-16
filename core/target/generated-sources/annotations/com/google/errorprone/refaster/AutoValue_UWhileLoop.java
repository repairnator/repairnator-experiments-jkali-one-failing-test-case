
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UWhileLoop extends UWhileLoop {

  private final UExpression condition;
  private final USimpleStatement statement;

  AutoValue_UWhileLoop(
      UExpression condition,
      USimpleStatement statement) {
    if (condition == null) {
      throw new NullPointerException("Null condition");
    }
    this.condition = condition;
    if (statement == null) {
      throw new NullPointerException("Null statement");
    }
    this.statement = statement;
  }

  @Override
  public UExpression getCondition() {
    return condition;
  }

  @Override
  public USimpleStatement getStatement() {
    return statement;
  }

  @Override
  public String toString() {
    return "UWhileLoop{"
         + "condition=" + condition + ", "
         + "statement=" + statement
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UWhileLoop) {
      UWhileLoop that = (UWhileLoop) o;
      return (this.condition.equals(that.getCondition()))
           && (this.statement.equals(that.getStatement()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.condition.hashCode();
    h *= 1000003;
    h ^= this.statement.hashCode();
    return h;
  }

}
