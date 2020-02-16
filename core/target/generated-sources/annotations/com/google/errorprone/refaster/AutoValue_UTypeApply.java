
package com.google.errorprone.refaster;

import java.util.List;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UTypeApply extends UTypeApply {

  private final UExpression type;
  private final List<UExpression> typeArguments;

  AutoValue_UTypeApply(
      UExpression type,
      List<UExpression> typeArguments) {
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
    if (typeArguments == null) {
      throw new NullPointerException("Null typeArguments");
    }
    this.typeArguments = typeArguments;
  }

  @Override
  public UExpression getType() {
    return type;
  }

  @Override
  public List<UExpression> getTypeArguments() {
    return typeArguments;
  }

  @Override
  public String toString() {
    return "UTypeApply{"
         + "type=" + type + ", "
         + "typeArguments=" + typeArguments
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UTypeApply) {
      UTypeApply that = (UTypeApply) o;
      return (this.type.equals(that.getType()))
           && (this.typeArguments.equals(that.getTypeArguments()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.type.hashCode();
    h *= 1000003;
    h ^= this.typeArguments.hashCode();
    return h;
  }

}
