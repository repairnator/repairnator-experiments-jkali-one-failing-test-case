
package com.google.errorprone.matchers;

import com.google.errorprone.VisitorState;
import com.sun.source.tree.Tree;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ChildMultiMatcher_Matchable<T extends Tree> extends ChildMultiMatcher.Matchable<T> {

  private final T tree;
  private final VisitorState state;

  AutoValue_ChildMultiMatcher_Matchable(
      T tree,
      VisitorState state) {
    if (tree == null) {
      throw new NullPointerException("Null tree");
    }
    this.tree = tree;
    if (state == null) {
      throw new NullPointerException("Null state");
    }
    this.state = state;
  }

  @Override
  public T tree() {
    return tree;
  }

  @Override
  public VisitorState state() {
    return state;
  }

  @Override
  public String toString() {
    return "Matchable{"
         + "tree=" + tree + ", "
         + "state=" + state
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ChildMultiMatcher.Matchable) {
      ChildMultiMatcher.Matchable<?> that = (ChildMultiMatcher.Matchable<?>) o;
      return (this.tree.equals(that.tree()))
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
    h ^= this.state.hashCode();
    return h;
  }

}
