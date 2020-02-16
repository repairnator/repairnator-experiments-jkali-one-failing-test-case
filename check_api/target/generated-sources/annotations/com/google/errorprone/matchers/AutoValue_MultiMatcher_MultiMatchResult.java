
package com.google.errorprone.matchers;

import com.sun.source.tree.Tree;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_MultiMatcher_MultiMatchResult<N extends Tree> extends MultiMatcher.MultiMatchResult<N> {

  private final boolean matches;
  private final List<N> matchingNodes;

  AutoValue_MultiMatcher_MultiMatchResult(
      boolean matches,
      List<N> matchingNodes) {
    this.matches = matches;
    if (matchingNodes == null) {
      throw new NullPointerException("Null matchingNodes");
    }
    this.matchingNodes = matchingNodes;
  }

  @Override
  public boolean matches() {
    return matches;
  }

  @Override
  public List<N> matchingNodes() {
    return matchingNodes;
  }

  @Override
  public String toString() {
    return "MultiMatchResult{"
         + "matches=" + matches + ", "
         + "matchingNodes=" + matchingNodes
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof MultiMatcher.MultiMatchResult) {
      MultiMatcher.MultiMatchResult<?> that = (MultiMatcher.MultiMatchResult<?>) o;
      return (this.matches == that.matches())
           && (this.matchingNodes.equals(that.matchingNodes()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.matches ? 1231 : 1237;
    h *= 1000003;
    h ^= this.matchingNodes.hashCode();
    return h;
  }

}
