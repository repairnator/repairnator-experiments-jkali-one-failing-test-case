
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;
import javax.lang.model.type.TypeKind;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UPrimitiveType extends UPrimitiveType {

  private final TypeKind kind;

  AutoValue_UPrimitiveType(
      TypeKind kind) {
    if (kind == null) {
      throw new NullPointerException("Null kind");
    }
    this.kind = kind;
  }

  @Override
  public TypeKind getKind() {
    return kind;
  }

  @Override
  public String toString() {
    return "UPrimitiveType{"
         + "kind=" + kind
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UPrimitiveType) {
      UPrimitiveType that = (UPrimitiveType) o;
      return (this.kind.equals(that.getKind()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.kind.hashCode();
    return h;
  }

}
