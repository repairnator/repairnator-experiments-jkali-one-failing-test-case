
package com.google.errorprone.refaster;

import com.sun.source.tree.Tree;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UOfKind extends UOfKind {

  private final UExpression expression;
  private final Set<Tree.Kind> allowed;

  AutoValue_UOfKind(
      UExpression expression,
      Set<Tree.Kind> allowed) {
    if (expression == null) {
      throw new NullPointerException("Null expression");
    }
    this.expression = expression;
    if (allowed == null) {
      throw new NullPointerException("Null allowed");
    }
    this.allowed = allowed;
  }

  @Override
  UExpression expression() {
    return expression;
  }

  @Override
  Set<Tree.Kind> allowed() {
    return allowed;
  }

  @Override
  public String toString() {
    return "UOfKind{"
         + "expression=" + expression + ", "
         + "allowed=" + allowed
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UOfKind) {
      UOfKind that = (UOfKind) o;
      return (this.expression.equals(that.expression()))
           && (this.allowed.equals(that.allowed()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.expression.hashCode();
    h *= 1000003;
    h ^= this.allowed.hashCode();
    return h;
  }

}
