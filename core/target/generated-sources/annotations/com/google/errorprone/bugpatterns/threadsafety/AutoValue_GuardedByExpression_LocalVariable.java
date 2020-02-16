
package com.google.errorprone.bugpatterns.threadsafety;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_GuardedByExpression_LocalVariable extends GuardedByExpression.LocalVariable {

  private final GuardedByExpression.Kind kind;
  private final Symbol sym;
  private final Type type;

  AutoValue_GuardedByExpression_LocalVariable(
      GuardedByExpression.Kind kind,
      Symbol sym,
      Type type) {
    if (kind == null) {
      throw new NullPointerException("Null kind");
    }
    this.kind = kind;
    if (sym == null) {
      throw new NullPointerException("Null sym");
    }
    this.sym = sym;
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
  }

  @Override
  public GuardedByExpression.Kind kind() {
    return kind;
  }

  @Override
  public Symbol sym() {
    return sym;
  }

  @Override
  public Type type() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof GuardedByExpression.LocalVariable) {
      GuardedByExpression.LocalVariable that = (GuardedByExpression.LocalVariable) o;
      return (this.kind.equals(that.kind()))
           && (this.sym.equals(that.sym()))
           && (this.type.equals(that.type()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.kind.hashCode();
    h *= 1000003;
    h ^= this.sym.hashCode();
    h *= 1000003;
    h ^= this.type.hashCode();
    return h;
  }

}
