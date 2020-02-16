
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableMap;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UPlaceholderExpression extends UPlaceholderExpression {

  private final PlaceholderMethod placeholder;
  private final ImmutableMap<UVariableDecl, UExpression> arguments;

  AutoValue_UPlaceholderExpression(
      PlaceholderMethod placeholder,
      ImmutableMap<UVariableDecl, UExpression> arguments) {
    if (placeholder == null) {
      throw new NullPointerException("Null placeholder");
    }
    this.placeholder = placeholder;
    if (arguments == null) {
      throw new NullPointerException("Null arguments");
    }
    this.arguments = arguments;
  }

  @Override
  PlaceholderMethod placeholder() {
    return placeholder;
  }

  @Override
  ImmutableMap<UVariableDecl, UExpression> arguments() {
    return arguments;
  }

  @Override
  public String toString() {
    return "UPlaceholderExpression{"
         + "placeholder=" + placeholder + ", "
         + "arguments=" + arguments
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UPlaceholderExpression) {
      UPlaceholderExpression that = (UPlaceholderExpression) o;
      return (this.placeholder.equals(that.placeholder()))
           && (this.arguments.equals(that.arguments()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.placeholder.hashCode();
    h *= 1000003;
    h ^= this.arguments.hashCode();
    return h;
  }

}
