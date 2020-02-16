
package com.google.errorprone.refaster;

import com.sun.source.tree.Tree;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UWildcard extends UWildcard {

  private final Tree.Kind kind;
  private final UTree<?> bound;

  AutoValue_UWildcard(
      Tree.Kind kind,
      @Nullable UTree<?> bound) {
    if (kind == null) {
      throw new NullPointerException("Null kind");
    }
    this.kind = kind;
    this.bound = bound;
  }

  @Override
  public Tree.Kind getKind() {
    return kind;
  }

  @Nullable
  @Override
  public UTree<?> getBound() {
    return bound;
  }

  @Override
  public String toString() {
    return "UWildcard{"
         + "kind=" + kind + ", "
         + "bound=" + bound
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UWildcard) {
      UWildcard that = (UWildcard) o;
      return (this.kind.equals(that.getKind()))
           && ((this.bound == null) ? (that.getBound() == null) : this.bound.equals(that.getBound()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.kind.hashCode();
    h *= 1000003;
    h ^= (bound == null) ? 0 : this.bound.hashCode();
    return h;
  }

}
