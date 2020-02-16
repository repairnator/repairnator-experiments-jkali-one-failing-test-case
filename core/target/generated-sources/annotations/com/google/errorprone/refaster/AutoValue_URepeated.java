
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_URepeated extends URepeated {

  private final String identifier;
  private final UExpression expression;

  AutoValue_URepeated(
      String identifier,
      UExpression expression) {
    if (identifier == null) {
      throw new NullPointerException("Null identifier");
    }
    this.identifier = identifier;
    if (expression == null) {
      throw new NullPointerException("Null expression");
    }
    this.expression = expression;
  }

  @Override
  String identifier() {
    return identifier;
  }

  @Override
  UExpression expression() {
    return expression;
  }

  @Override
  public String toString() {
    return "URepeated{"
         + "identifier=" + identifier + ", "
         + "expression=" + expression
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof URepeated) {
      URepeated that = (URepeated) o;
      return (this.identifier.equals(that.identifier()))
           && (this.expression.equals(that.expression()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.identifier.hashCode();
    h *= 1000003;
    h ^= this.expression.hashCode();
    return h;
  }

}
