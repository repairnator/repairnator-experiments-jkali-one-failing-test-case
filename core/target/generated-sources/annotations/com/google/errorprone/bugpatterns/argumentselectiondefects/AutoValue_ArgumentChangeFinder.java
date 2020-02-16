
package com.google.errorprone.bugpatterns.argumentselectiondefects;

import com.google.common.collect.ImmutableList;
import java.util.function.Function;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ArgumentChangeFinder extends ArgumentChangeFinder {

  private final Function<ParameterPair, Double> distanceFunction;
  private final ImmutableList<Heuristic> heuristics;

  private AutoValue_ArgumentChangeFinder(
      Function<ParameterPair, Double> distanceFunction,
      ImmutableList<Heuristic> heuristics) {
    this.distanceFunction = distanceFunction;
    this.heuristics = heuristics;
  }

  @Override
  Function<ParameterPair, Double> distanceFunction() {
    return distanceFunction;
  }

  @Override
  ImmutableList<Heuristic> heuristics() {
    return heuristics;
  }

  @Override
  public String toString() {
    return "ArgumentChangeFinder{"
         + "distanceFunction=" + distanceFunction + ", "
         + "heuristics=" + heuristics
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ArgumentChangeFinder) {
      ArgumentChangeFinder that = (ArgumentChangeFinder) o;
      return (this.distanceFunction.equals(that.distanceFunction()))
           && (this.heuristics.equals(that.heuristics()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.distanceFunction.hashCode();
    h *= 1000003;
    h ^= this.heuristics.hashCode();
    return h;
  }

  static final class Builder extends ArgumentChangeFinder.Builder {
    private Function<ParameterPair, Double> distanceFunction;
    private ImmutableList.Builder<Heuristic> heuristicsBuilder$;
    private ImmutableList<Heuristic> heuristics;
    Builder() {
    }
    @Override
    ArgumentChangeFinder.Builder setDistanceFunction(Function<ParameterPair, Double> distanceFunction) {
      if (distanceFunction == null) {
        throw new NullPointerException("Null distanceFunction");
      }
      this.distanceFunction = distanceFunction;
      return this;
    }
    @Override
    ImmutableList.Builder<Heuristic> heuristicsBuilder() {
      if (heuristicsBuilder$ == null) {
        heuristicsBuilder$ = ImmutableList.builder();
      }
      return heuristicsBuilder$;
    }
    @Override
    ArgumentChangeFinder build() {
      if (heuristicsBuilder$ != null) {
        this.heuristics = heuristicsBuilder$.build();
      } else if (this.heuristics == null) {
        this.heuristics = ImmutableList.of();
      }
      String missing = "";
      if (this.distanceFunction == null) {
        missing += " distanceFunction";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_ArgumentChangeFinder(
          this.distanceFunction,
          this.heuristics);
    }
  }

}
