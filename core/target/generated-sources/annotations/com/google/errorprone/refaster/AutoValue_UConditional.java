
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UConditional extends UConditional {

  private final UExpression condition;
  private final UExpression trueExpression;
  private final UExpression falseExpression;

  AutoValue_UConditional(
      UExpression condition,
      UExpression trueExpression,
      UExpression falseExpression) {
    if (condition == null) {
      throw new NullPointerException("Null condition");
    }
    this.condition = condition;
    if (trueExpression == null) {
      throw new NullPointerException("Null trueExpression");
    }
    this.trueExpression = trueExpression;
    if (falseExpression == null) {
      throw new NullPointerException("Null falseExpression");
    }
    this.falseExpression = falseExpression;
  }

  @Override
  public UExpression getCondition() {
    return condition;
  }

  @Override
  public UExpression getTrueExpression() {
    return trueExpression;
  }

  @Override
  public UExpression getFalseExpression() {
    return falseExpression;
  }

  @Override
  public String toString() {
    return "UConditional{"
         + "condition=" + condition + ", "
         + "trueExpression=" + trueExpression + ", "
         + "falseExpression=" + falseExpression
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UConditional) {
      UConditional that = (UConditional) o;
      return (this.condition.equals(that.getCondition()))
           && (this.trueExpression.equals(that.getTrueExpression()))
           && (this.falseExpression.equals(that.getFalseExpression()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.condition.hashCode();
    h *= 1000003;
    h ^= this.trueExpression.hashCode();
    h *= 1000003;
    h ^= this.falseExpression.hashCode();
    return h;
  }

}
