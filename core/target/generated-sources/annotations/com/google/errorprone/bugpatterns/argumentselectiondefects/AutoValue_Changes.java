
package com.google.errorprone.bugpatterns.argumentselectiondefects;

import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_Changes extends Changes {

  private final ImmutableList<Double> originalCost;
  private final ImmutableList<Double> assignmentCost;
  private final ImmutableList<ParameterPair> changedPairs;

  AutoValue_Changes(
      ImmutableList<Double> originalCost,
      ImmutableList<Double> assignmentCost,
      ImmutableList<ParameterPair> changedPairs) {
    if (originalCost == null) {
      throw new NullPointerException("Null originalCost");
    }
    this.originalCost = originalCost;
    if (assignmentCost == null) {
      throw new NullPointerException("Null assignmentCost");
    }
    this.assignmentCost = assignmentCost;
    if (changedPairs == null) {
      throw new NullPointerException("Null changedPairs");
    }
    this.changedPairs = changedPairs;
  }

  @Override
  ImmutableList<Double> originalCost() {
    return originalCost;
  }

  @Override
  ImmutableList<Double> assignmentCost() {
    return assignmentCost;
  }

  @Override
  ImmutableList<ParameterPair> changedPairs() {
    return changedPairs;
  }

  @Override
  public String toString() {
    return "Changes{"
         + "originalCost=" + originalCost + ", "
         + "assignmentCost=" + assignmentCost + ", "
         + "changedPairs=" + changedPairs
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Changes) {
      Changes that = (Changes) o;
      return (this.originalCost.equals(that.originalCost()))
           && (this.assignmentCost.equals(that.assignmentCost()))
           && (this.changedPairs.equals(that.changedPairs()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.originalCost.hashCode();
    h *= 1000003;
    h ^= this.assignmentCost.hashCode();
    h *= 1000003;
    h ^= this.changedPairs.hashCode();
    return h;
  }

}
