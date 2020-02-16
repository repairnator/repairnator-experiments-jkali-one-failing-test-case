
package com.google.errorprone.bugpatterns.apidiff;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ApiDiff extends ApiDiff {

  private final ImmutableSet<String> unsupportedClasses;
  private final ImmutableSetMultimap<String, ApiDiff.ClassMemberKey> unsupportedMembersByClass;

  AutoValue_ApiDiff(
      ImmutableSet<String> unsupportedClasses,
      ImmutableSetMultimap<String, ApiDiff.ClassMemberKey> unsupportedMembersByClass) {
    if (unsupportedClasses == null) {
      throw new NullPointerException("Null unsupportedClasses");
    }
    this.unsupportedClasses = unsupportedClasses;
    if (unsupportedMembersByClass == null) {
      throw new NullPointerException("Null unsupportedMembersByClass");
    }
    this.unsupportedMembersByClass = unsupportedMembersByClass;
  }

  @Override
  public ImmutableSet<String> unsupportedClasses() {
    return unsupportedClasses;
  }

  @Override
  public ImmutableSetMultimap<String, ApiDiff.ClassMemberKey> unsupportedMembersByClass() {
    return unsupportedMembersByClass;
  }

  @Override
  public String toString() {
    return "ApiDiff{"
         + "unsupportedClasses=" + unsupportedClasses + ", "
         + "unsupportedMembersByClass=" + unsupportedMembersByClass
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ApiDiff) {
      ApiDiff that = (ApiDiff) o;
      return (this.unsupportedClasses.equals(that.unsupportedClasses()))
           && (this.unsupportedMembersByClass.equals(that.unsupportedMembersByClass()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.unsupportedClasses.hashCode();
    h *= 1000003;
    h ^= this.unsupportedMembersByClass.hashCode();
    return h;
  }

}
