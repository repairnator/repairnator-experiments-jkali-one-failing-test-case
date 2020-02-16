
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UMemberSelect extends UMemberSelect {

  private final UExpression getExpression;
  private final StringName getIdentifier;
  private final UType type;

  AutoValue_UMemberSelect(
      UExpression getExpression,
      StringName getIdentifier,
      UType type) {
    if (getExpression == null) {
      throw new NullPointerException("Null getExpression");
    }
    this.getExpression = getExpression;
    if (getIdentifier == null) {
      throw new NullPointerException("Null getIdentifier");
    }
    this.getIdentifier = getIdentifier;
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
  }

  @Override
  public UExpression getExpression() {
    return getExpression;
  }

  @Override
  public StringName getIdentifier() {
    return getIdentifier;
  }

  @Override
  UType type() {
    return type;
  }

  @Override
  public String toString() {
    return "UMemberSelect{"
         + "getExpression=" + getExpression + ", "
         + "getIdentifier=" + getIdentifier + ", "
         + "type=" + type
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UMemberSelect) {
      UMemberSelect that = (UMemberSelect) o;
      return (this.getExpression.equals(that.getExpression()))
           && (this.getIdentifier.equals(that.getIdentifier()))
           && (this.type.equals(that.type()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.getExpression.hashCode();
    h *= 1000003;
    h ^= this.getIdentifier.hashCode();
    h *= 1000003;
    h ^= this.type.hashCode();
    return h;
  }

}
