
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UAnyOf extends UAnyOf {

  private final ImmutableList<UExpression> expressions;

  AutoValue_UAnyOf(
      ImmutableList<UExpression> expressions) {
    if (expressions == null) {
      throw new NullPointerException("Null expressions");
    }
    this.expressions = expressions;
  }

  @Override
  ImmutableList<UExpression> expressions() {
    return expressions;
  }

  @Override
  public String toString() {
    return "UAnyOf{"
         + "expressions=" + expressions
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UAnyOf) {
      UAnyOf that = (UAnyOf) o;
      return (this.expressions.equals(that.expressions()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.expressions.hashCode();
    return h;
  }

}
