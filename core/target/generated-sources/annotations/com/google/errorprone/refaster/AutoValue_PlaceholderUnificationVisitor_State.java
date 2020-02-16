
package com.google.errorprone.refaster;

import com.sun.tools.javac.util.List;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_PlaceholderUnificationVisitor_State<R> extends PlaceholderUnificationVisitor.State<R> {

  private final List<UVariableDecl> seenParameters;
  private final Unifier unifier;
  private final R result;

  AutoValue_PlaceholderUnificationVisitor_State(
      List<UVariableDecl> seenParameters,
      Unifier unifier,
      @Nullable R result) {
    if (seenParameters == null) {
      throw new NullPointerException("Null seenParameters");
    }
    this.seenParameters = seenParameters;
    if (unifier == null) {
      throw new NullPointerException("Null unifier");
    }
    this.unifier = unifier;
    this.result = result;
  }

  @Override
  public List<UVariableDecl> seenParameters() {
    return seenParameters;
  }

  @Override
  public Unifier unifier() {
    return unifier;
  }

  @Nullable
  @Override
  public R result() {
    return result;
  }

  @Override
  public String toString() {
    return "State{"
         + "seenParameters=" + seenParameters + ", "
         + "unifier=" + unifier + ", "
         + "result=" + result
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof PlaceholderUnificationVisitor.State) {
      PlaceholderUnificationVisitor.State<?> that = (PlaceholderUnificationVisitor.State<?>) o;
      return (this.seenParameters.equals(that.seenParameters()))
           && (this.unifier.equals(that.unifier()))
           && ((this.result == null) ? (that.result() == null) : this.result.equals(that.result()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.seenParameters.hashCode();
    h *= 1000003;
    h ^= this.unifier.hashCode();
    h *= 1000003;
    h ^= (result == null) ? 0 : this.result.hashCode();
    return h;
  }

}
