
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_USynchronized extends USynchronized {

  private final UExpression expression;
  private final UBlock block;

  AutoValue_USynchronized(
      UExpression expression,
      UBlock block) {
    if (expression == null) {
      throw new NullPointerException("Null expression");
    }
    this.expression = expression;
    if (block == null) {
      throw new NullPointerException("Null block");
    }
    this.block = block;
  }

  @Override
  public UExpression getExpression() {
    return expression;
  }

  @Override
  public UBlock getBlock() {
    return block;
  }

  @Override
  public String toString() {
    return "USynchronized{"
         + "expression=" + expression + ", "
         + "block=" + block
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof USynchronized) {
      USynchronized that = (USynchronized) o;
      return (this.expression.equals(that.getExpression()))
           && (this.block.equals(that.getBlock()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.expression.hashCode();
    h *= 1000003;
    h ^= this.block.hashCode();
    return h;
  }

}
