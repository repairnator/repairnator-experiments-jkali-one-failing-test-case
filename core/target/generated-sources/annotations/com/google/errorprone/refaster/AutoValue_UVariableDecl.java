
package com.google.errorprone.refaster;

import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UVariableDecl extends UVariableDecl {

  private final StringName name;
  private final UExpression type;
  private final UExpression initializer;

  AutoValue_UVariableDecl(
      StringName name,
      UExpression type,
      @Nullable UExpression initializer) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
    this.initializer = initializer;
  }

  @Override
  public StringName getName() {
    return name;
  }

  @Override
  public UExpression getType() {
    return type;
  }

  @Nullable
  @Override
  public UExpression getInitializer() {
    return initializer;
  }

  @Override
  public String toString() {
    return "UVariableDecl{"
         + "name=" + name + ", "
         + "type=" + type + ", "
         + "initializer=" + initializer
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UVariableDecl) {
      UVariableDecl that = (UVariableDecl) o;
      return (this.name.equals(that.getName()))
           && (this.type.equals(that.getType()))
           && ((this.initializer == null) ? (that.getInitializer() == null) : this.initializer.equals(that.getInitializer()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.name.hashCode();
    h *= 1000003;
    h ^= this.type.hashCode();
    h *= 1000003;
    h ^= (initializer == null) ? 0 : this.initializer.hashCode();
    return h;
  }

}
