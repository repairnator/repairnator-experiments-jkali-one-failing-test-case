
package com.google.errorprone.refaster;

import com.sun.source.tree.Tree;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UUnary extends UUnary {

  private final Tree.Kind kind;
  private final UExpression expression;

  AutoValue_UUnary(
      Tree.Kind kind,
      UExpression expression) {
    if (kind == null) {
      throw new NullPointerException("Null kind");
    }
    this.kind = kind;
    if (expression == null) {
      throw new NullPointerException("Null expression");
    }
    this.expression = expression;
  }

  @Override
  public Tree.Kind getKind() {
    return kind;
  }

  @Override
  public UExpression getExpression() {
    return expression;
  }

  @Override
  public String toString() {
    return "UUnary{"
         + "kind=" + kind + ", "
         + "expression=" + expression
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UUnary) {
      UUnary that = (UUnary) o;
      return (this.kind.equals(that.getKind()))
           && (this.expression.equals(that.getExpression()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.kind.hashCode();
    h *= 1000003;
    h ^= this.expression.hashCode();
    return h;
  }

}
