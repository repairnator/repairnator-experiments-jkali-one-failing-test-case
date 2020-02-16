
package com.google.errorprone.dataflow;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import javax.lang.model.element.Element;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_AccessPath extends AccessPath {

  private final Element base;
  private final ImmutableList<String> path;

  AutoValue_AccessPath(
      @Nullable Element base,
      ImmutableList<String> path) {
    this.base = base;
    if (path == null) {
      throw new NullPointerException("Null path");
    }
    this.path = path;
  }

  @Nullable
  @Override
  public Element base() {
    return base;
  }

  @Override
  public ImmutableList<String> path() {
    return path;
  }

  @Override
  public String toString() {
    return "AccessPath{"
         + "base=" + base + ", "
         + "path=" + path
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof AccessPath) {
      AccessPath that = (AccessPath) o;
      return ((this.base == null) ? (that.base() == null) : this.base.equals(that.base()))
           && (this.path.equals(that.path()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (base == null) ? 0 : this.base.hashCode();
    h *= 1000003;
    h ^= this.path.hashCode();
    return h;
  }

}
