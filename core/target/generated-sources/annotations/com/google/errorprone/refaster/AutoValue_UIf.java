
package com.google.errorprone.refaster;

import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UIf extends UIf {

  private final UExpression condition;
  private final UStatement thenStatement;
  private final UStatement elseStatement;

  AutoValue_UIf(
      UExpression condition,
      UStatement thenStatement,
      @Nullable UStatement elseStatement) {
    if (condition == null) {
      throw new NullPointerException("Null condition");
    }
    this.condition = condition;
    if (thenStatement == null) {
      throw new NullPointerException("Null thenStatement");
    }
    this.thenStatement = thenStatement;
    this.elseStatement = elseStatement;
  }

  @Override
  public UExpression getCondition() {
    return condition;
  }

  @Override
  public UStatement getThenStatement() {
    return thenStatement;
  }

  @Nullable
  @Override
  public UStatement getElseStatement() {
    return elseStatement;
  }

  @Override
  public String toString() {
    return "UIf{"
         + "condition=" + condition + ", "
         + "thenStatement=" + thenStatement + ", "
         + "elseStatement=" + elseStatement
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UIf) {
      UIf that = (UIf) o;
      return (this.condition.equals(that.getCondition()))
           && (this.thenStatement.equals(that.getThenStatement()))
           && ((this.elseStatement == null) ? (that.getElseStatement() == null) : this.elseStatement.equals(that.getElseStatement()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.condition.hashCode();
    h *= 1000003;
    h ^= this.thenStatement.hashCode();
    h *= 1000003;
    h ^= (elseStatement == null) ? 0 : this.elseStatement.hashCode();
    return h;
  }

}
