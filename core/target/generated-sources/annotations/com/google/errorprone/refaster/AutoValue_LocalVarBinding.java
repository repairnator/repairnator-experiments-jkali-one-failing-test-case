
package com.google.errorprone.refaster;

import com.sun.source.tree.ModifiersTree;
import com.sun.tools.javac.code.Symbol;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_LocalVarBinding extends LocalVarBinding {

  private final Symbol.VarSymbol symbol;
  private final ModifiersTree modifiers;

  AutoValue_LocalVarBinding(
      Symbol.VarSymbol symbol,
      ModifiersTree modifiers) {
    if (symbol == null) {
      throw new NullPointerException("Null symbol");
    }
    this.symbol = symbol;
    if (modifiers == null) {
      throw new NullPointerException("Null modifiers");
    }
    this.modifiers = modifiers;
  }

  @Override
  public Symbol.VarSymbol getSymbol() {
    return symbol;
  }

  @Override
  public ModifiersTree getModifiers() {
    return modifiers;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof LocalVarBinding) {
      LocalVarBinding that = (LocalVarBinding) o;
      return (this.symbol.equals(that.getSymbol()))
           && (this.modifiers.equals(that.getModifiers()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.symbol.hashCode();
    h *= 1000003;
    h ^= this.modifiers.hashCode();
    return h;
  }

}
