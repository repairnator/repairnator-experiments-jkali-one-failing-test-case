
package com.google.errorprone.refaster;

import com.sun.source.tree.Tree;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ULiteral extends ULiteral {

  private final Tree.Kind kind;
  private final Object value;

  AutoValue_ULiteral(
      Tree.Kind kind,
      @Nullable Object value) {
    if (kind == null) {
      throw new NullPointerException("Null kind");
    }
    this.kind = kind;
    this.value = value;
  }

  @Override
  public Tree.Kind getKind() {
    return kind;
  }

  @Nullable
  @Override
  public Object getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "ULiteral{"
         + "kind=" + kind + ", "
         + "value=" + value
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ULiteral) {
      ULiteral that = (ULiteral) o;
      return (this.kind.equals(that.getKind()))
           && ((this.value == null) ? (that.getValue() == null) : this.value.equals(that.getValue()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.kind.hashCode();
    h *= 1000003;
    h ^= (value == null) ? 0 : this.value.hashCode();
    return h;
  }

}
