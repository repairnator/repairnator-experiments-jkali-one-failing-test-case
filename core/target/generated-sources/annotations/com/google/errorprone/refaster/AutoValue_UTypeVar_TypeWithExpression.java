
package com.google.errorprone.refaster;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UTypeVar_TypeWithExpression extends UTypeVar.TypeWithExpression {

  private final Type type;
  private final JCTree.JCExpression expression;

  AutoValue_UTypeVar_TypeWithExpression(
      Type type,
      @Nullable JCTree.JCExpression expression) {
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
    this.expression = expression;
  }

  @Override
  public Type type() {
    return type;
  }

  @Nullable
  @Override
  JCTree.JCExpression expression() {
    return expression;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UTypeVar.TypeWithExpression) {
      UTypeVar.TypeWithExpression that = (UTypeVar.TypeWithExpression) o;
      return (this.type.equals(that.type()))
           && ((this.expression == null) ? (that.expression() == null) : this.expression.equals(that.expression()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.type.hashCode();
    h *= 1000003;
    h ^= (expression == null) ? 0 : this.expression.hashCode();
    return h;
  }

}
