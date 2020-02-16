
package com.google.errorprone.refaster;

import com.sun.tools.javac.code.TypeTag;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UPrimitiveTypeTree extends UPrimitiveTypeTree {

  private final TypeTag typeTag;

  AutoValue_UPrimitiveTypeTree(
      TypeTag typeTag) {
    if (typeTag == null) {
      throw new NullPointerException("Null typeTag");
    }
    this.typeTag = typeTag;
  }

  @Override
  TypeTag typeTag() {
    return typeTag;
  }

  @Override
  public String toString() {
    return "UPrimitiveTypeTree{"
         + "typeTag=" + typeTag
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UPrimitiveTypeTree) {
      UPrimitiveTypeTree that = (UPrimitiveTypeTree) o;
      return (this.typeTag.equals(that.typeTag()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.typeTag.hashCode();
    return h;
  }

}
