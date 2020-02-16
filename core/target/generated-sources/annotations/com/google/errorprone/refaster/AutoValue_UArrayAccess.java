
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UArrayAccess extends UArrayAccess {

  private final UExpression expression;
  private final UExpression index;

  AutoValue_UArrayAccess(
      UExpression expression,
      UExpression index) {
    if (expression == null) {
      throw new NullPointerException("Null expression");
    }
    this.expression = expression;
    if (index == null) {
      throw new NullPointerException("Null index");
    }
    this.index = index;
  }

  @Override
  public UExpression getExpression() {
    return expression;
  }

  @Override
  public UExpression getIndex() {
    return index;
  }

  @Override
  public String toString() {
    return "UArrayAccess{"
         + "expression=" + expression + ", "
         + "index=" + index
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UArrayAccess) {
      UArrayAccess that = (UArrayAccess) o;
      return (this.expression.equals(that.getExpression()))
           && (this.index.equals(that.getIndex()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.expression.hashCode();
    h *= 1000003;
    h ^= this.index.hashCode();
    return h;
  }

}
