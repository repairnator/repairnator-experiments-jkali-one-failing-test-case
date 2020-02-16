
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UMethodDecl extends UMethodDecl {

  private final UModifiers modifiers;
  private final StringName name;
  private final UExpression returnType;
  private final ImmutableList<UVariableDecl> parameters;
  private final ImmutableList<UExpression> throws0;
  private final UBlock body;

  AutoValue_UMethodDecl(
      UModifiers modifiers,
      StringName name,
      UExpression returnType,
      ImmutableList<UVariableDecl> parameters,
      ImmutableList<UExpression> throws0,
      UBlock body) {
    if (modifiers == null) {
      throw new NullPointerException("Null modifiers");
    }
    this.modifiers = modifiers;
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    if (returnType == null) {
      throw new NullPointerException("Null returnType");
    }
    this.returnType = returnType;
    if (parameters == null) {
      throw new NullPointerException("Null parameters");
    }
    this.parameters = parameters;
    if (throws0 == null) {
      throw new NullPointerException("Null throws");
    }
    this.throws0 = throws0;
    if (body == null) {
      throw new NullPointerException("Null body");
    }
    this.body = body;
  }

  @Override
  public UModifiers getModifiers() {
    return modifiers;
  }

  @Override
  public StringName getName() {
    return name;
  }

  @Override
  public UExpression getReturnType() {
    return returnType;
  }

  @Override
  public ImmutableList<UVariableDecl> getParameters() {
    return parameters;
  }

  @Override
  public ImmutableList<UExpression> getThrows() {
    return throws0;
  }

  @Override
  public UBlock getBody() {
    return body;
  }

  @Override
  public String toString() {
    return "UMethodDecl{"
         + "modifiers=" + modifiers + ", "
         + "name=" + name + ", "
         + "returnType=" + returnType + ", "
         + "parameters=" + parameters + ", "
         + "throws=" + throws0 + ", "
         + "body=" + body
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UMethodDecl) {
      UMethodDecl that = (UMethodDecl) o;
      return (this.modifiers.equals(that.getModifiers()))
           && (this.name.equals(that.getName()))
           && (this.returnType.equals(that.getReturnType()))
           && (this.parameters.equals(that.getParameters()))
           && (this.throws0.equals(that.getThrows()))
           && (this.body.equals(that.getBody()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.modifiers.hashCode();
    h *= 1000003;
    h ^= this.name.hashCode();
    h *= 1000003;
    h ^= this.returnType.hashCode();
    h *= 1000003;
    h ^= this.parameters.hashCode();
    h *= 1000003;
    h ^= this.throws0.hashCode();
    h *= 1000003;
    h ^= this.body.hashCode();
    return h;
  }

}
