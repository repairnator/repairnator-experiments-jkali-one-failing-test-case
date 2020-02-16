
package com.google.errorprone.refaster;

import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UForLoop extends UForLoop {

  private final List<UStatement> initializer;
  private final UExpression condition;
  private final List<UExpressionStatement> update;
  private final USimpleStatement statement;

  AutoValue_UForLoop(
      List<UStatement> initializer,
      @Nullable UExpression condition,
      List<UExpressionStatement> update,
      USimpleStatement statement) {
    if (initializer == null) {
      throw new NullPointerException("Null initializer");
    }
    this.initializer = initializer;
    this.condition = condition;
    if (update == null) {
      throw new NullPointerException("Null update");
    }
    this.update = update;
    if (statement == null) {
      throw new NullPointerException("Null statement");
    }
    this.statement = statement;
  }

  @Override
  public List<UStatement> getInitializer() {
    return initializer;
  }

  @Nullable
  @Override
  public UExpression getCondition() {
    return condition;
  }

  @Override
  public List<UExpressionStatement> getUpdate() {
    return update;
  }

  @Override
  public USimpleStatement getStatement() {
    return statement;
  }

  @Override
  public String toString() {
    return "UForLoop{"
         + "initializer=" + initializer + ", "
         + "condition=" + condition + ", "
         + "update=" + update + ", "
         + "statement=" + statement
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UForLoop) {
      UForLoop that = (UForLoop) o;
      return (this.initializer.equals(that.getInitializer()))
           && ((this.condition == null) ? (that.getCondition() == null) : this.condition.equals(that.getCondition()))
           && (this.update.equals(that.getUpdate()))
           && (this.statement.equals(that.getStatement()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.initializer.hashCode();
    h *= 1000003;
    h ^= (condition == null) ? 0 : this.condition.hashCode();
    h *= 1000003;
    h ^= this.update.hashCode();
    h *= 1000003;
    h ^= this.statement.hashCode();
    return h;
  }

}
