
package com.google.errorprone.util;

import com.google.common.collect.ImmutableList;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.parser.Tokens;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_Commented<T extends Tree> extends Commented<T> {

  private final T tree;
  private final ImmutableList<Tokens.Comment> beforeComments;
  private final ImmutableList<Tokens.Comment> afterComments;

  private AutoValue_Commented(
      T tree,
      ImmutableList<Tokens.Comment> beforeComments,
      ImmutableList<Tokens.Comment> afterComments) {
    this.tree = tree;
    this.beforeComments = beforeComments;
    this.afterComments = afterComments;
  }

  @Override
  public T tree() {
    return tree;
  }

  @Override
  public ImmutableList<Tokens.Comment> beforeComments() {
    return beforeComments;
  }

  @Override
  public ImmutableList<Tokens.Comment> afterComments() {
    return afterComments;
  }

  @Override
  public String toString() {
    return "Commented{"
         + "tree=" + tree + ", "
         + "beforeComments=" + beforeComments + ", "
         + "afterComments=" + afterComments
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Commented) {
      Commented<?> that = (Commented<?>) o;
      return (this.tree.equals(that.tree()))
           && (this.beforeComments.equals(that.beforeComments()))
           && (this.afterComments.equals(that.afterComments()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.tree.hashCode();
    h *= 1000003;
    h ^= this.beforeComments.hashCode();
    h *= 1000003;
    h ^= this.afterComments.hashCode();
    return h;
  }

  static final class Builder<T extends Tree> extends Commented.Builder<T> {
    private T tree;
    private ImmutableList.Builder<Tokens.Comment> beforeCommentsBuilder$;
    private ImmutableList<Tokens.Comment> beforeComments;
    private ImmutableList.Builder<Tokens.Comment> afterCommentsBuilder$;
    private ImmutableList<Tokens.Comment> afterComments;
    Builder() {
    }
    @Override
    Commented.Builder<T> setTree(T tree) {
      if (tree == null) {
        throw new NullPointerException("Null tree");
      }
      this.tree = tree;
      return this;
    }
    @Override
    protected ImmutableList.Builder<Tokens.Comment> beforeCommentsBuilder() {
      if (beforeCommentsBuilder$ == null) {
        beforeCommentsBuilder$ = ImmutableList.builder();
      }
      return beforeCommentsBuilder$;
    }
    @Override
    protected ImmutableList.Builder<Tokens.Comment> afterCommentsBuilder() {
      if (afterCommentsBuilder$ == null) {
        afterCommentsBuilder$ = ImmutableList.builder();
      }
      return afterCommentsBuilder$;
    }
    @Override
    Commented<T> build() {
      if (beforeCommentsBuilder$ != null) {
        this.beforeComments = beforeCommentsBuilder$.build();
      } else if (this.beforeComments == null) {
        this.beforeComments = ImmutableList.of();
      }
      if (afterCommentsBuilder$ != null) {
        this.afterComments = afterCommentsBuilder$.build();
      } else if (this.afterComments == null) {
        this.afterComments = ImmutableList.of();
      }
      String missing = "";
      if (this.tree == null) {
        missing += " tree";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_Commented<T>(
          this.tree,
          this.beforeComments,
          this.afterComments);
    }
  }

}
