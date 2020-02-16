
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UDoWhileLoop extends UDoWhileLoop {

  private final USimpleStatement statement;
  private final UExpression condition;

  AutoValue_UDoWhileLoop(
      USimpleStatement statement,
      UExpression condition) {
    if (statement == null) {
      throw new NullPointerException("Null statement");
    }
    this.statement = statement;
    if (condition == null) {
      throw new NullPointerException("Null condition");
    }
    this.condition = condition;
  }

  @Override
  public USimpleStatement getStatement() {
    return statement;
  }

  @Override
  public UExpression getCondition() {
    return condition;
  }

  @Override
  public String toString() {
    return "UDoWhileLoop{"
         + "statement=" + statement + ", "
         + "condition=" + condition
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UDoWhileLoop) {
      UDoWhileLoop that = (UDoWhileLoop) o;
      return (this.statement.equals(that.getStatement()))
           && (this.condition.equals(that.getCondition()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.statement.hashCode();
    h *= 1000003;
    h ^= this.condition.hashCode();
    return h;
  }

}
