
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UEnhancedForLoop extends UEnhancedForLoop {

  private final UVariableDecl variable;
  private final UExpression expression;
  private final USimpleStatement statement;

  AutoValue_UEnhancedForLoop(
      UVariableDecl variable,
      UExpression expression,
      USimpleStatement statement) {
    if (variable == null) {
      throw new NullPointerException("Null variable");
    }
    this.variable = variable;
    if (expression == null) {
      throw new NullPointerException("Null expression");
    }
    this.expression = expression;
    if (statement == null) {
      throw new NullPointerException("Null statement");
    }
    this.statement = statement;
  }

  @Override
  public UVariableDecl getVariable() {
    return variable;
  }

  @Override
  public UExpression getExpression() {
    return expression;
  }

  @Override
  public USimpleStatement getStatement() {
    return statement;
  }

  @Override
  public String toString() {
    return "UEnhancedForLoop{"
         + "variable=" + variable + ", "
         + "expression=" + expression + ", "
         + "statement=" + statement
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UEnhancedForLoop) {
      UEnhancedForLoop that = (UEnhancedForLoop) o;
      return (this.variable.equals(that.getVariable()))
           && (this.expression.equals(that.getExpression()))
           && (this.statement.equals(that.getStatement()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.variable.hashCode();
    h *= 1000003;
    h ^= this.expression.hashCode();
    h *= 1000003;
    h ^= this.statement.hashCode();
    return h;
  }

}
