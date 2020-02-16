
package com.google.errorprone.bugpatterns;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ProtoRedundantSet_FieldWithValue extends ProtoRedundantSet.FieldWithValue {

  private final ProtoRedundantSet.ProtoField field;
  private final MethodInvocationTree methodInvocation;
  private final ExpressionTree argument;

  AutoValue_ProtoRedundantSet_FieldWithValue(
      ProtoRedundantSet.ProtoField field,
      MethodInvocationTree methodInvocation,
      ExpressionTree argument) {
    if (field == null) {
      throw new NullPointerException("Null field");
    }
    this.field = field;
    if (methodInvocation == null) {
      throw new NullPointerException("Null methodInvocation");
    }
    this.methodInvocation = methodInvocation;
    if (argument == null) {
      throw new NullPointerException("Null argument");
    }
    this.argument = argument;
  }

  @Override
  ProtoRedundantSet.ProtoField getField() {
    return field;
  }

  @Override
  MethodInvocationTree getMethodInvocation() {
    return methodInvocation;
  }

  @Override
  ExpressionTree getArgument() {
    return argument;
  }

  @Override
  public String toString() {
    return "FieldWithValue{"
         + "field=" + field + ", "
         + "methodInvocation=" + methodInvocation + ", "
         + "argument=" + argument
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ProtoRedundantSet.FieldWithValue) {
      ProtoRedundantSet.FieldWithValue that = (ProtoRedundantSet.FieldWithValue) o;
      return (this.field.equals(that.getField()))
           && (this.methodInvocation.equals(that.getMethodInvocation()))
           && (this.argument.equals(that.getArgument()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.field.hashCode();
    h *= 1000003;
    h ^= this.methodInvocation.hashCode();
    h *= 1000003;
    h ^= this.argument.hashCode();
    return h;
  }

}
