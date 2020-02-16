
package com.google.errorprone.dataflow.nullnesspropagation.inference;

import com.google.common.collect.ImmutableList;
import com.sun.source.tree.Tree;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_TypeArgInferenceVar extends TypeArgInferenceVar {

  private final ImmutableList<Integer> typeArgSelector;
  private final Tree astNode;

  AutoValue_TypeArgInferenceVar(
      ImmutableList<Integer> typeArgSelector,
      Tree astNode) {
    if (typeArgSelector == null) {
      throw new NullPointerException("Null typeArgSelector");
    }
    this.typeArgSelector = typeArgSelector;
    if (astNode == null) {
      throw new NullPointerException("Null astNode");
    }
    this.astNode = astNode;
  }

  @Override
  ImmutableList<Integer> typeArgSelector() {
    return typeArgSelector;
  }

  @Override
  Tree astNode() {
    return astNode;
  }

  @Override
  public String toString() {
    return "TypeArgInferenceVar{"
         + "typeArgSelector=" + typeArgSelector + ", "
         + "astNode=" + astNode
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof TypeArgInferenceVar) {
      TypeArgInferenceVar that = (TypeArgInferenceVar) o;
      return (this.typeArgSelector.equals(that.typeArgSelector()))
           && (this.astNode.equals(that.astNode()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.typeArgSelector.hashCode();
    h *= 1000003;
    h ^= this.astNode.hashCode();
    return h;
  }

}
