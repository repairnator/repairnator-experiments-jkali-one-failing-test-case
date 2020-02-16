
package com.google.errorprone.bugpatterns.argumentselectiondefects;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Type;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_Parameter extends Parameter {

  private final String name;
  private final Type type;
  private final int index;
  private final String text;
  private final Tree.Kind kind;
  private final boolean constant;

  AutoValue_Parameter(
      String name,
      Type type,
      int index,
      String text,
      Tree.Kind kind,
      boolean constant) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
    this.index = index;
    if (text == null) {
      throw new NullPointerException("Null text");
    }
    this.text = text;
    if (kind == null) {
      throw new NullPointerException("Null kind");
    }
    this.kind = kind;
    this.constant = constant;
  }

  @Override
  String name() {
    return name;
  }

  @Override
  Type type() {
    return type;
  }

  @Override
  int index() {
    return index;
  }

  @Override
  String text() {
    return text;
  }

  @Override
  Tree.Kind kind() {
    return kind;
  }

  @Override
  boolean constant() {
    return constant;
  }

  @Override
  public String toString() {
    return "Parameter{"
         + "name=" + name + ", "
         + "type=" + type + ", "
         + "index=" + index + ", "
         + "text=" + text + ", "
         + "kind=" + kind + ", "
         + "constant=" + constant
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Parameter) {
      Parameter that = (Parameter) o;
      return (this.name.equals(that.name()))
           && (this.type.equals(that.type()))
           && (this.index == that.index())
           && (this.text.equals(that.text()))
           && (this.kind.equals(that.kind()))
           && (this.constant == that.constant());
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
    h ^= this.index;
    h *= 1000003;
    h ^= this.text.hashCode();
    h *= 1000003;
    h ^= this.kind.hashCode();
    h *= 1000003;
    h ^= this.constant ? 1231 : 1237;
    return h;
  }

}
