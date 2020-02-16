
package com.google.errorprone.bugpatterns;

import com.sun.source.tree.Tree;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_EqualsWrongThing_ComparisonSite extends EqualsWrongThing.ComparisonSite {

  private final Tree tree;
  private final EqualsWrongThing.ComparisonPair pair;

  AutoValue_EqualsWrongThing_ComparisonSite(
      Tree tree,
      EqualsWrongThing.ComparisonPair pair) {
    if (tree == null) {
      throw new NullPointerException("Null tree");
    }
    this.tree = tree;
    if (pair == null) {
      throw new NullPointerException("Null pair");
    }
    this.pair = pair;
  }

  @Override
  Tree tree() {
    return tree;
  }

  @Override
  EqualsWrongThing.ComparisonPair pair() {
    return pair;
  }

  @Override
  public String toString() {
    return "ComparisonSite{"
         + "tree=" + tree + ", "
         + "pair=" + pair
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof EqualsWrongThing.ComparisonSite) {
      EqualsWrongThing.ComparisonSite that = (EqualsWrongThing.ComparisonSite) o;
      return (this.tree.equals(that.tree()))
           && (this.pair.equals(that.pair()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.tree.hashCode();
    h *= 1000003;
    h ^= this.pair.hashCode();
    return h;
  }

}
