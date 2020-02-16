
package com.google.errorprone.bugpatterns;

import com.sun.source.tree.Tree;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UngroupedOverloads_MemberWithIndex extends UngroupedOverloads.MemberWithIndex {

  private final int index;
  private final Tree tree;

  AutoValue_UngroupedOverloads_MemberWithIndex(
      int index,
      Tree tree) {
    this.index = index;
    if (tree == null) {
      throw new NullPointerException("Null tree");
    }
    this.tree = tree;
  }

  @Override
  int index() {
    return index;
  }

  @Override
  Tree tree() {
    return tree;
  }

  @Override
  public String toString() {
    return "MemberWithIndex{"
         + "index=" + index + ", "
         + "tree=" + tree
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UngroupedOverloads.MemberWithIndex) {
      UngroupedOverloads.MemberWithIndex that = (UngroupedOverloads.MemberWithIndex) o;
      return (this.index == that.index())
           && (this.tree.equals(that.tree()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.index;
    h *= 1000003;
    h ^= this.tree.hashCode();
    return h;
  }

}
