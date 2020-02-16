
package com.google.errorprone.bugpatterns.apidiff;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ApiDiff_ClassMemberKey extends ApiDiff.ClassMemberKey {

  private final String identifier;
  private final String descriptor;

  AutoValue_ApiDiff_ClassMemberKey(
      String identifier,
      String descriptor) {
    if (identifier == null) {
      throw new NullPointerException("Null identifier");
    }
    this.identifier = identifier;
    if (descriptor == null) {
      throw new NullPointerException("Null descriptor");
    }
    this.descriptor = descriptor;
  }

  @Override
  public String identifier() {
    return identifier;
  }

  @Override
  public String descriptor() {
    return descriptor;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ApiDiff.ClassMemberKey) {
      ApiDiff.ClassMemberKey that = (ApiDiff.ClassMemberKey) o;
      return (this.identifier.equals(that.identifier()))
           && (this.descriptor.equals(that.descriptor()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.identifier.hashCode();
    h *= 1000003;
    h ^= this.descriptor.hashCode();
    return h;
  }

}
