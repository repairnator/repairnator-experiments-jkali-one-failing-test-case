
package com.google.errorprone.util;

import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Type;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ASTHelpers_TargetType extends ASTHelpers.TargetType {

  private final Type type;
  private final TreePath path;

  AutoValue_ASTHelpers_TargetType(
      Type type,
      TreePath path) {
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
    if (path == null) {
      throw new NullPointerException("Null path");
    }
    this.path = path;
  }

  @Override
  public Type type() {
    return type;
  }

  @Override
  public TreePath path() {
    return path;
  }

  @Override
  public String toString() {
    return "TargetType{"
         + "type=" + type + ", "
         + "path=" + path
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ASTHelpers.TargetType) {
      ASTHelpers.TargetType that = (ASTHelpers.TargetType) o;
      return (this.type.equals(that.type()))
           && (this.path.equals(that.path()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.type.hashCode();
    h *= 1000003;
    h ^= this.path.hashCode();
    return h;
  }

}
