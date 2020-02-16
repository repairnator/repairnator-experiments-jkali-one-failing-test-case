
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableMap;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UPlaceholderStatement extends UPlaceholderStatement {

  private final PlaceholderMethod placeholder;
  private final ImmutableMap<UVariableDecl, UExpression> arguments;
  private final ControlFlowVisitor.Result implementationFlow;

  AutoValue_UPlaceholderStatement(
      PlaceholderMethod placeholder,
      ImmutableMap<UVariableDecl, UExpression> arguments,
      ControlFlowVisitor.Result implementationFlow) {
    if (placeholder == null) {
      throw new NullPointerException("Null placeholder");
    }
    this.placeholder = placeholder;
    if (arguments == null) {
      throw new NullPointerException("Null arguments");
    }
    this.arguments = arguments;
    if (implementationFlow == null) {
      throw new NullPointerException("Null implementationFlow");
    }
    this.implementationFlow = implementationFlow;
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
  ControlFlowVisitor.Result implementationFlow() {
    return implementationFlow;
  }

  @Override
  public String toString() {
    return "UPlaceholderStatement{"
         + "placeholder=" + placeholder + ", "
         + "arguments=" + arguments + ", "
         + "implementationFlow=" + implementationFlow
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UPlaceholderStatement) {
      UPlaceholderStatement that = (UPlaceholderStatement) o;
      return (this.placeholder.equals(that.placeholder()))
           && (this.arguments.equals(that.arguments()))
           && (this.implementationFlow.equals(that.implementationFlow()));
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
    h *= 1000003;
    h ^= this.implementationFlow.hashCode();
    return h;
  }

}
