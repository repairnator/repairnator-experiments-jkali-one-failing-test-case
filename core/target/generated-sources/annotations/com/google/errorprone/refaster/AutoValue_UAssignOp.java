
package com.google.errorprone.refaster;

import com.sun.source.tree.Tree;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UAssignOp extends UAssignOp {

  private final UExpression variable;
  private final Tree.Kind kind;
  private final UExpression expression;

  AutoValue_UAssignOp(
      UExpression variable,
      Tree.Kind kind,
      UExpression expression) {
    if (variable == null) {
      throw new NullPointerException("Null variable");
    }
    this.variable = variable;
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
  public UExpression getVariable() {
    return variable;
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
    return "UAssignOp{"
         + "variable=" + variable + ", "
         + "kind=" + kind + ", "
         + "expression=" + expression
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UAssignOp) {
      UAssignOp that = (UAssignOp) o;
      return (this.variable.equals(that.getVariable()))
           && (this.kind.equals(that.getKind()))
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
    h ^= this.kind.hashCode();
    h *= 1000003;
    h ^= this.expression.hashCode();
    return h;
  }

}
