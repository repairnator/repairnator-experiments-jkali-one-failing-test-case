
package com.google.errorprone.bugpatterns.overloading;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ParameterTrie_Parameter extends ParameterTrie.Parameter {

  private final ParameterTree tree;
  private final int position;

  AutoValue_ParameterTrie_Parameter(
      ParameterTree tree,
      int position) {
    if (tree == null) {
      throw new NullPointerException("Null tree");
    }
    this.tree = tree;
    this.position = position;
  }

  @Override
  public ParameterTree tree() {
    return tree;
  }

  @Override
  public int position() {
    return position;
  }

  @Override
  public String toString() {
    return "Parameter{"
         + "tree=" + tree + ", "
         + "position=" + position
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ParameterTrie.Parameter) {
      ParameterTrie.Parameter that = (ParameterTrie.Parameter) o;
      return (this.tree.equals(that.tree()))
           && (this.position == that.position());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.tree.hashCode();
    h *= 1000003;
    h ^= this.position;
    return h;
  }

}
