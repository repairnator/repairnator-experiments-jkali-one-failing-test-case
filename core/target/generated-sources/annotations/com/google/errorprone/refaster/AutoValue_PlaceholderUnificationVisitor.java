
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableMap;
import com.sun.tools.javac.tree.TreeMaker;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_PlaceholderUnificationVisitor extends PlaceholderUnificationVisitor {

  private final TreeMaker maker;
  private final ImmutableMap<UVariableDecl, UExpression> arguments;

  AutoValue_PlaceholderUnificationVisitor(
      TreeMaker maker,
      ImmutableMap<UVariableDecl, UExpression> arguments) {
    if (maker == null) {
      throw new NullPointerException("Null maker");
    }
    this.maker = maker;
    if (arguments == null) {
      throw new NullPointerException("Null arguments");
    }
    this.arguments = arguments;
  }

  @Override
  TreeMaker maker() {
    return maker;
  }

  @Override
  ImmutableMap<UVariableDecl, UExpression> arguments() {
    return arguments;
  }

  @Override
  public String toString() {
    return "PlaceholderUnificationVisitor{"
         + "maker=" + maker + ", "
         + "arguments=" + arguments
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof PlaceholderUnificationVisitor) {
      PlaceholderUnificationVisitor that = (PlaceholderUnificationVisitor) o;
      return (this.maker.equals(that.maker()))
           && (this.arguments.equals(that.arguments()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.maker.hashCode();
    h *= 1000003;
    h ^= this.arguments.hashCode();
    return h;
  }

}
