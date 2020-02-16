
package com.google.errorprone.refaster;

import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UNewClass extends UNewClass {

  private final UExpression enclosingExpression;
  private final List<UExpression> typeArguments;
  private final UExpression identifier;
  private final List<UExpression> arguments;
  private final UClassDecl classBody;

  AutoValue_UNewClass(
      @Nullable UExpression enclosingExpression,
      List<UExpression> typeArguments,
      UExpression identifier,
      List<UExpression> arguments,
      @Nullable UClassDecl classBody) {
    this.enclosingExpression = enclosingExpression;
    if (typeArguments == null) {
      throw new NullPointerException("Null typeArguments");
    }
    this.typeArguments = typeArguments;
    if (identifier == null) {
      throw new NullPointerException("Null identifier");
    }
    this.identifier = identifier;
    if (arguments == null) {
      throw new NullPointerException("Null arguments");
    }
    this.arguments = arguments;
    this.classBody = classBody;
  }

  @Nullable
  @Override
  public UExpression getEnclosingExpression() {
    return enclosingExpression;
  }

  @Override
  public List<UExpression> getTypeArguments() {
    return typeArguments;
  }

  @Override
  public UExpression getIdentifier() {
    return identifier;
  }

  @Override
  public List<UExpression> getArguments() {
    return arguments;
  }

  @Nullable
  @Override
  public UClassDecl getClassBody() {
    return classBody;
  }

  @Override
  public String toString() {
    return "UNewClass{"
         + "enclosingExpression=" + enclosingExpression + ", "
         + "typeArguments=" + typeArguments + ", "
         + "identifier=" + identifier + ", "
         + "arguments=" + arguments + ", "
         + "classBody=" + classBody
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UNewClass) {
      UNewClass that = (UNewClass) o;
      return ((this.enclosingExpression == null) ? (that.getEnclosingExpression() == null) : this.enclosingExpression.equals(that.getEnclosingExpression()))
           && (this.typeArguments.equals(that.getTypeArguments()))
           && (this.identifier.equals(that.getIdentifier()))
           && (this.arguments.equals(that.getArguments()))
           && ((this.classBody == null) ? (that.getClassBody() == null) : this.classBody.equals(that.getClassBody()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (enclosingExpression == null) ? 0 : this.enclosingExpression.hashCode();
    h *= 1000003;
    h ^= this.typeArguments.hashCode();
    h *= 1000003;
    h ^= this.identifier.hashCode();
    h *= 1000003;
    h ^= this.arguments.hashCode();
    h *= 1000003;
    h ^= (classBody == null) ? 0 : this.classBody.hashCode();
    return h;
  }

}
