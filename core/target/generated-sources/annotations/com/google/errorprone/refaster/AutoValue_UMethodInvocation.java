
package com.google.errorprone.refaster;

import java.util.List;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UMethodInvocation extends UMethodInvocation {

  private final UExpression methodSelect;
  private final List<UExpression> arguments;

  AutoValue_UMethodInvocation(
      UExpression methodSelect,
      List<UExpression> arguments) {
    if (methodSelect == null) {
      throw new NullPointerException("Null methodSelect");
    }
    this.methodSelect = methodSelect;
    if (arguments == null) {
      throw new NullPointerException("Null arguments");
    }
    this.arguments = arguments;
  }

  @Override
  public UExpression getMethodSelect() {
    return methodSelect;
  }

  @Override
  public List<UExpression> getArguments() {
    return arguments;
  }

  @Override
  public String toString() {
    return "UMethodInvocation{"
         + "methodSelect=" + methodSelect + ", "
         + "arguments=" + arguments
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UMethodInvocation) {
      UMethodInvocation that = (UMethodInvocation) o;
      return (this.methodSelect.equals(that.getMethodSelect()))
           && (this.arguments.equals(that.getArguments()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.methodSelect.hashCode();
    h *= 1000003;
    h ^= this.arguments.hashCode();
    return h;
  }

}
