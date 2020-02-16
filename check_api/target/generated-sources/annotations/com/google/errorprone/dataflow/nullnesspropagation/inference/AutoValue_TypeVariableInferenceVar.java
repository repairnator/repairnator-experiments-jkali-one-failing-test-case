
package com.google.errorprone.dataflow.nullnesspropagation.inference;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.tools.javac.code.Symbol;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_TypeVariableInferenceVar extends TypeVariableInferenceVar {

  private final Symbol.TypeVariableSymbol typeVar;
  private final MethodInvocationTree typeApplicationSite;

  AutoValue_TypeVariableInferenceVar(
      Symbol.TypeVariableSymbol typeVar,
      MethodInvocationTree typeApplicationSite) {
    if (typeVar == null) {
      throw new NullPointerException("Null typeVar");
    }
    this.typeVar = typeVar;
    if (typeApplicationSite == null) {
      throw new NullPointerException("Null typeApplicationSite");
    }
    this.typeApplicationSite = typeApplicationSite;
  }

  @Override
  Symbol.TypeVariableSymbol typeVar() {
    return typeVar;
  }

  @Override
  MethodInvocationTree typeApplicationSite() {
    return typeApplicationSite;
  }

  @Override
  public String toString() {
    return "TypeVariableInferenceVar{"
         + "typeVar=" + typeVar + ", "
         + "typeApplicationSite=" + typeApplicationSite
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof TypeVariableInferenceVar) {
      TypeVariableInferenceVar that = (TypeVariableInferenceVar) o;
      return (this.typeVar.equals(that.typeVar()))
           && (this.typeApplicationSite.equals(that.typeApplicationSite()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.typeVar.hashCode();
    h *= 1000003;
    h ^= this.typeApplicationSite.hashCode();
    return h;
  }

}
