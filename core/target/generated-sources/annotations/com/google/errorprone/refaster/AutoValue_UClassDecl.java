
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UClassDecl extends UClassDecl {

  private final ImmutableList<UMethodDecl> members;

  AutoValue_UClassDecl(
      ImmutableList<UMethodDecl> members) {
    if (members == null) {
      throw new NullPointerException("Null members");
    }
    this.members = members;
  }

  @Override
  public ImmutableList<UMethodDecl> getMembers() {
    return members;
  }

  @Override
  public String toString() {
    return "UClassDecl{"
         + "members=" + members
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UClassDecl) {
      UClassDecl that = (UClassDecl) o;
      return (this.members.equals(that.getMembers()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.members.hashCode();
    return h;
  }

}
