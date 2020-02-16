
package com.google.errorprone.bugpatterns.inject.guice;

import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Type;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_AssistedParameters_ConflictResult extends AssistedParameters.ConflictResult {

  private final Type type;
  private final String value;
  private final List<VariableTree> parameters;

  AutoValue_AssistedParameters_ConflictResult(
      Type type,
      String value,
      List<VariableTree> parameters) {
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
    if (value == null) {
      throw new NullPointerException("Null value");
    }
    this.value = value;
    if (parameters == null) {
      throw new NullPointerException("Null parameters");
    }
    this.parameters = parameters;
  }

  @Override
  Type type() {
    return type;
  }

  @Override
  String value() {
    return value;
  }

  @Override
  List<VariableTree> parameters() {
    return parameters;
  }

  @Override
  public String toString() {
    return "ConflictResult{"
         + "type=" + type + ", "
         + "value=" + value + ", "
         + "parameters=" + parameters
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof AssistedParameters.ConflictResult) {
      AssistedParameters.ConflictResult that = (AssistedParameters.ConflictResult) o;
      return (this.type.equals(that.type()))
           && (this.value.equals(that.value()))
           && (this.parameters.equals(that.parameters()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.type.hashCode();
    h *= 1000003;
    h ^= this.value.hashCode();
    h *= 1000003;
    h ^= this.parameters.hashCode();
    return h;
  }

}
