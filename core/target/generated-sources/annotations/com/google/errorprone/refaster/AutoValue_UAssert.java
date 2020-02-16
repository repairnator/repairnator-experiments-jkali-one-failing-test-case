
package com.google.errorprone.refaster;

import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UAssert extends UAssert {

  private final UExpression condition;
  private final UExpression detail;

  AutoValue_UAssert(
      UExpression condition,
      @Nullable UExpression detail) {
    if (condition == null) {
      throw new NullPointerException("Null condition");
    }
    this.condition = condition;
    this.detail = detail;
  }

  @Override
  public UExpression getCondition() {
    return condition;
  }

  @Nullable
  @Override
  public UExpression getDetail() {
    return detail;
  }

  @Override
  public String toString() {
    return "UAssert{"
         + "condition=" + condition + ", "
         + "detail=" + detail
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UAssert) {
      UAssert that = (UAssert) o;
      return (this.condition.equals(that.getCondition()))
           && ((this.detail == null) ? (that.getDetail() == null) : this.detail.equals(that.getDetail()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.condition.hashCode();
    h *= 1000003;
    h ^= (detail == null) ? 0 : this.detail.hashCode();
    return h;
  }

}
