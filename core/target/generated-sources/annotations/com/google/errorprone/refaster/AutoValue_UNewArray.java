
package com.google.errorprone.refaster;

import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UNewArray extends UNewArray {

  private final UExpression type;
  private final List<UExpression> dimensions;
  private final List<UExpression> initializers;

  AutoValue_UNewArray(
      @Nullable UExpression type,
      @Nullable List<UExpression> dimensions,
      @Nullable List<UExpression> initializers) {
    this.type = type;
    this.dimensions = dimensions;
    this.initializers = initializers;
  }

  @Nullable
  @Override
  public UExpression getType() {
    return type;
  }

  @Nullable
  @Override
  public List<UExpression> getDimensions() {
    return dimensions;
  }

  @Nullable
  @Override
  public List<UExpression> getInitializers() {
    return initializers;
  }

  @Override
  public String toString() {
    return "UNewArray{"
         + "type=" + type + ", "
         + "dimensions=" + dimensions + ", "
         + "initializers=" + initializers
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UNewArray) {
      UNewArray that = (UNewArray) o;
      return ((this.type == null) ? (that.getType() == null) : this.type.equals(that.getType()))
           && ((this.dimensions == null) ? (that.getDimensions() == null) : this.dimensions.equals(that.getDimensions()))
           && ((this.initializers == null) ? (that.getInitializers() == null) : this.initializers.equals(that.getInitializers()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (type == null) ? 0 : this.type.hashCode();
    h *= 1000003;
    h ^= (dimensions == null) ? 0 : this.dimensions.hashCode();
    h *= 1000003;
    h ^= (initializers == null) ? 0 : this.initializers.hashCode();
    return h;
  }

}
