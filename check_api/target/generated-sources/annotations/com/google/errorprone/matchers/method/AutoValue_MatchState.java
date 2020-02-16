
package com.google.errorprone.matchers.method;

import com.google.common.collect.ImmutableList;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_MatchState extends MatchState {

  private final Type ownerType;
  private final Symbol.MethodSymbol sym;
  private final ImmutableList<Type> paramTypes;

  AutoValue_MatchState(
      Type ownerType,
      Symbol.MethodSymbol sym,
      ImmutableList<Type> paramTypes) {
    if (ownerType == null) {
      throw new NullPointerException("Null ownerType");
    }
    this.ownerType = ownerType;
    if (sym == null) {
      throw new NullPointerException("Null sym");
    }
    this.sym = sym;
    if (paramTypes == null) {
      throw new NullPointerException("Null paramTypes");
    }
    this.paramTypes = paramTypes;
  }

  @Override
  Type ownerType() {
    return ownerType;
  }

  @Override
  Symbol.MethodSymbol sym() {
    return sym;
  }

  @Override
  ImmutableList<Type> paramTypes() {
    return paramTypes;
  }

  @Override
  public String toString() {
    return "MatchState{"
         + "ownerType=" + ownerType + ", "
         + "sym=" + sym + ", "
         + "paramTypes=" + paramTypes
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof MatchState) {
      MatchState that = (MatchState) o;
      return (this.ownerType.equals(that.ownerType()))
           && (this.sym.equals(that.sym()))
           && (this.paramTypes.equals(that.paramTypes()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.ownerType.hashCode();
    h *= 1000003;
    h ^= this.sym.hashCode();
    h *= 1000003;
    h ^= this.paramTypes.hashCode();
    return h;
  }

}
