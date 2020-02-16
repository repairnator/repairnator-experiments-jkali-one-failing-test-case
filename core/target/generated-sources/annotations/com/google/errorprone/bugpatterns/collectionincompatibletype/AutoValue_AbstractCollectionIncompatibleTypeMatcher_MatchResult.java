
package com.google.errorprone.bugpatterns.collectionincompatibletype;

import com.sun.source.tree.ExpressionTree;
import com.sun.tools.javac.code.Type;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_AbstractCollectionIncompatibleTypeMatcher_MatchResult extends AbstractCollectionIncompatibleTypeMatcher.MatchResult {

  private final ExpressionTree sourceTree;
  private final Type sourceType;
  private final Type targetType;
  private final AbstractCollectionIncompatibleTypeMatcher matcher;

  AutoValue_AbstractCollectionIncompatibleTypeMatcher_MatchResult(
      ExpressionTree sourceTree,
      Type sourceType,
      Type targetType,
      AbstractCollectionIncompatibleTypeMatcher matcher) {
    if (sourceTree == null) {
      throw new NullPointerException("Null sourceTree");
    }
    this.sourceTree = sourceTree;
    if (sourceType == null) {
      throw new NullPointerException("Null sourceType");
    }
    this.sourceType = sourceType;
    if (targetType == null) {
      throw new NullPointerException("Null targetType");
    }
    this.targetType = targetType;
    if (matcher == null) {
      throw new NullPointerException("Null matcher");
    }
    this.matcher = matcher;
  }

  @Override
  public ExpressionTree sourceTree() {
    return sourceTree;
  }

  @Override
  public Type sourceType() {
    return sourceType;
  }

  @Override
  public Type targetType() {
    return targetType;
  }

  @Override
  public AbstractCollectionIncompatibleTypeMatcher matcher() {
    return matcher;
  }

  @Override
  public String toString() {
    return "MatchResult{"
         + "sourceTree=" + sourceTree + ", "
         + "sourceType=" + sourceType + ", "
         + "targetType=" + targetType + ", "
         + "matcher=" + matcher
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof AbstractCollectionIncompatibleTypeMatcher.MatchResult) {
      AbstractCollectionIncompatibleTypeMatcher.MatchResult that = (AbstractCollectionIncompatibleTypeMatcher.MatchResult) o;
      return (this.sourceTree.equals(that.sourceTree()))
           && (this.sourceType.equals(that.sourceType()))
           && (this.targetType.equals(that.targetType()))
           && (this.matcher.equals(that.matcher()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.sourceTree.hashCode();
    h *= 1000003;
    h ^= this.sourceType.hashCode();
    h *= 1000003;
    h ^= this.targetType.hashCode();
    h *= 1000003;
    h ^= this.matcher.hashCode();
    return h;
  }

}
