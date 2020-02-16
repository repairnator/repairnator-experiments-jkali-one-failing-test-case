
package com.google.errorprone.bugpatterns.overloading;

import com.sun.source.tree.Tree;
import javax.annotation.processing.Generated;
import javax.lang.model.element.Name;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ParameterTree extends ParameterTree {

  private final Name name;
  private final Tree type;
  private final boolean varArgs;

  AutoValue_ParameterTree(
      Name name,
      Tree type,
      boolean varArgs) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
    this.varArgs = varArgs;
  }

  @Override
  public Name getName() {
    return name;
  }

  @Override
  public Tree getType() {
    return type;
  }

  @Override
  public boolean isVarArgs() {
    return varArgs;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ParameterTree) {
      ParameterTree that = (ParameterTree) o;
      return (this.name.equals(that.getName()))
           && (this.type.equals(that.getType()))
           && (this.varArgs == that.isVarArgs());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.name.hashCode();
    h *= 1000003;
    h ^= this.type.hashCode();
    h *= 1000003;
    h ^= this.varArgs ? 1231 : 1237;
    return h;
  }

}
