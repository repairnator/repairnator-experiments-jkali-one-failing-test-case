
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UClassDecl_UnifierWithRemainingMembers extends UClassDecl.UnifierWithRemainingMembers {

  private final Unifier unifier;
  private final ImmutableList<UMethodDecl> remainingMembers;

  AutoValue_UClassDecl_UnifierWithRemainingMembers(
      Unifier unifier,
      ImmutableList<UMethodDecl> remainingMembers) {
    if (unifier == null) {
      throw new NullPointerException("Null unifier");
    }
    this.unifier = unifier;
    if (remainingMembers == null) {
      throw new NullPointerException("Null remainingMembers");
    }
    this.remainingMembers = remainingMembers;
  }

  @Override
  Unifier unifier() {
    return unifier;
  }

  @Override
  ImmutableList<UMethodDecl> remainingMembers() {
    return remainingMembers;
  }

  @Override
  public String toString() {
    return "UnifierWithRemainingMembers{"
         + "unifier=" + unifier + ", "
         + "remainingMembers=" + remainingMembers
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UClassDecl.UnifierWithRemainingMembers) {
      UClassDecl.UnifierWithRemainingMembers that = (UClassDecl.UnifierWithRemainingMembers) o;
      return (this.unifier.equals(that.unifier()))
           && (this.remainingMembers.equals(that.remainingMembers()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.unifier.hashCode();
    h *= 1000003;
    h ^= this.remainingMembers.hashCode();
    return h;
  }

}
