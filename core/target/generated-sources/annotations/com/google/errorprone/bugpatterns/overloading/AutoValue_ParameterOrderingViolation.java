
package com.google.errorprone.bugpatterns.overloading;

import com.google.common.collect.ImmutableList;
import com.sun.source.tree.MethodTree;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ParameterOrderingViolation extends ParameterOrderingViolation {

  private final MethodTree methodTree;
  private final ImmutableList<ParameterTree> actual;
  private final ImmutableList<ParameterTree> expected;

  private AutoValue_ParameterOrderingViolation(
      MethodTree methodTree,
      ImmutableList<ParameterTree> actual,
      ImmutableList<ParameterTree> expected) {
    this.methodTree = methodTree;
    this.actual = actual;
    this.expected = expected;
  }

  @Override
  public MethodTree methodTree() {
    return methodTree;
  }

  @Override
  public ImmutableList<ParameterTree> actual() {
    return actual;
  }

  @Override
  public ImmutableList<ParameterTree> expected() {
    return expected;
  }

  @Override
  public String toString() {
    return "ParameterOrderingViolation{"
         + "methodTree=" + methodTree + ", "
         + "actual=" + actual + ", "
         + "expected=" + expected
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ParameterOrderingViolation) {
      ParameterOrderingViolation that = (ParameterOrderingViolation) o;
      return (this.methodTree.equals(that.methodTree()))
           && (this.actual.equals(that.actual()))
           && (this.expected.equals(that.expected()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.methodTree.hashCode();
    h *= 1000003;
    h ^= this.actual.hashCode();
    h *= 1000003;
    h ^= this.expected.hashCode();
    return h;
  }

  static final class Builder extends ParameterOrderingViolation.Builder {
    private MethodTree methodTree;
    private ImmutableList<ParameterTree> actual;
    private ImmutableList<ParameterTree> expected;
    Builder() {
    }
    @Override
    ParameterOrderingViolation.Builder setMethodTree(MethodTree methodTree) {
      if (methodTree == null) {
        throw new NullPointerException("Null methodTree");
      }
      this.methodTree = methodTree;
      return this;
    }
    @Override
    ParameterOrderingViolation.Builder setActual(ImmutableList<ParameterTree> actual) {
      if (actual == null) {
        throw new NullPointerException("Null actual");
      }
      this.actual = actual;
      return this;
    }
    @Override
    ParameterOrderingViolation.Builder setExpected(ImmutableList<ParameterTree> expected) {
      if (expected == null) {
        throw new NullPointerException("Null expected");
      }
      this.expected = expected;
      return this;
    }
    @Override
    ParameterOrderingViolation autoBuild() {
      String missing = "";
      if (this.methodTree == null) {
        missing += " methodTree";
      }
      if (this.actual == null) {
        missing += " actual";
      }
      if (this.expected == null) {
        missing += " expected";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_ParameterOrderingViolation(
          this.methodTree,
          this.actual,
          this.expected);
    }
  }

}
