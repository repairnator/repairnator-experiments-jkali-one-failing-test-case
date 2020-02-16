
package com.google.errorprone.bugpatterns.argumentselectiondefects;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.VisitorState;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Symbol;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_InvocationInfo extends InvocationInfo {

  private final Tree tree;
  private final Symbol.MethodSymbol symbol;
  private final ImmutableList<? extends ExpressionTree> actualParameters;
  private final ImmutableList<Symbol.VarSymbol> formalParameters;
  private final VisitorState state;

  AutoValue_InvocationInfo(
      Tree tree,
      Symbol.MethodSymbol symbol,
      ImmutableList<? extends ExpressionTree> actualParameters,
      ImmutableList<Symbol.VarSymbol> formalParameters,
      VisitorState state) {
    if (tree == null) {
      throw new NullPointerException("Null tree");
    }
    this.tree = tree;
    if (symbol == null) {
      throw new NullPointerException("Null symbol");
    }
    this.symbol = symbol;
    if (actualParameters == null) {
      throw new NullPointerException("Null actualParameters");
    }
    this.actualParameters = actualParameters;
    if (formalParameters == null) {
      throw new NullPointerException("Null formalParameters");
    }
    this.formalParameters = formalParameters;
    if (state == null) {
      throw new NullPointerException("Null state");
    }
    this.state = state;
  }

  @Override
  Tree tree() {
    return tree;
  }

  @Override
  Symbol.MethodSymbol symbol() {
    return symbol;
  }

  @Override
  ImmutableList<? extends ExpressionTree> actualParameters() {
    return actualParameters;
  }

  @Override
  ImmutableList<Symbol.VarSymbol> formalParameters() {
    return formalParameters;
  }

  @Override
  VisitorState state() {
    return state;
  }

  @Override
  public String toString() {
    return "InvocationInfo{"
         + "tree=" + tree + ", "
         + "symbol=" + symbol + ", "
         + "actualParameters=" + actualParameters + ", "
         + "formalParameters=" + formalParameters + ", "
         + "state=" + state
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof InvocationInfo) {
      InvocationInfo that = (InvocationInfo) o;
      return (this.tree.equals(that.tree()))
           && (this.symbol.equals(that.symbol()))
           && (this.actualParameters.equals(that.actualParameters()))
           && (this.formalParameters.equals(that.formalParameters()))
           && (this.state.equals(that.state()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.tree.hashCode();
    h *= 1000003;
    h ^= this.symbol.hashCode();
    h *= 1000003;
    h ^= this.actualParameters.hashCode();
    h *= 1000003;
    h ^= this.formalParameters.hashCode();
    h *= 1000003;
    h ^= this.state.hashCode();
    return h;
  }

}
