
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UAssign extends UAssign {

  private final UExpression variable;
  private final UExpression expression;

  AutoValue_UAssign(
      UExpression variable,
      UExpression expression) {
    if (variable == null) {
      throw new NullPointerException("Null variable");
    }
    this.variable = variable;
    if (expression == null) {
      throw new NullPointerException("Null expression");
    }
    this.expression = expression;
  }

  @Override
  public UExpression getVariable() {
    return variable;
  }

  @Override
  public UExpression getExpression() {
    return expression;
  }

  @Override
  public String toString() {
    return "UAssign{"
         + "variable=" + variable + ", "
         + "expression=" + expression
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UAssign) {
      UAssign that = (UAssign) o;
      return (this.variable.equals(that.getVariable()))
           && (this.expression.equals(that.getExpression()));
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
    return h;
  }

}
