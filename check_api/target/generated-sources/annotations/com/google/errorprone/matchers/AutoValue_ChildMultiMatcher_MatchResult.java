
package com.google.errorprone.matchers;

import com.sun.source.tree.Tree;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ChildMultiMatcher_MatchResult<T extends Tree> extends ChildMultiMatcher.MatchResult<T> {

  private final List<T> matchingNodes;
  private final boolean matches;

  AutoValue_ChildMultiMatcher_MatchResult(
      List<T> matchingNodes,
      boolean matches) {
    if (matchingNodes == null) {
      throw new NullPointerException("Null matchingNodes");
    }
    this.matchingNodes = matchingNodes;
    this.matches = matches;
  }

  @Override
  public List<T> matchingNodes() {
    return matchingNodes;
  }

  @Override
  public boolean matches() {
    return matches;
  }

  @Override
  public String toString() {
    return "MatchResult{"
         + "matchingNodes=" + matchingNodes + ", "
         + "matches=" + matches
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ChildMultiMatcher.MatchResult) {
      ChildMultiMatcher.MatchResult<?> that = (ChildMultiMatcher.MatchResult<?>) o;
      return (this.matchingNodes.equals(that.matchingNodes()))
           && (this.matches == that.matches());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.matchingNodes.hashCode();
    h *= 1000003;
    h ^= this.matches ? 1231 : 1237;
    return h;
  }

}
