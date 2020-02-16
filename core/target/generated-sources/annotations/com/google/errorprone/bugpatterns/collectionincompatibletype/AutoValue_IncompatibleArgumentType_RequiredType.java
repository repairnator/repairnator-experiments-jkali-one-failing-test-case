
package com.google.errorprone.bugpatterns.collectionincompatibletype;

import com.sun.tools.javac.code.Type;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_IncompatibleArgumentType_RequiredType extends IncompatibleArgumentType.RequiredType {

  private final Type type;

  AutoValue_IncompatibleArgumentType_RequiredType(
      @Nullable Type type) {
    this.type = type;
  }

  @Nullable
  @Override
  Type type() {
    return type;
  }

  @Override
  public String toString() {
    return "RequiredType{"
         + "type=" + type
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof IncompatibleArgumentType.RequiredType) {
      IncompatibleArgumentType.RequiredType that = (IncompatibleArgumentType.RequiredType) o;
      return ((this.type == null) ? (that.type() == null) : this.type.equals(that.type()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (type == null) ? 0 : this.type.hashCode();
    return h;
  }

}
