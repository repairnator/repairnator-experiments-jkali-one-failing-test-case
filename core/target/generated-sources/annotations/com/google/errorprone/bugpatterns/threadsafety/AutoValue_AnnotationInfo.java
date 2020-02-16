
package com.google.errorprone.bugpatterns.threadsafety;

import com.google.common.collect.ImmutableSet;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_AnnotationInfo extends AnnotationInfo {

  private final String typeName;
  private final ImmutableSet<String> internalContainerOf;

  AutoValue_AnnotationInfo(
      String typeName,
      ImmutableSet<String> internalContainerOf) {
    if (typeName == null) {
      throw new NullPointerException("Null typeName");
    }
    this.typeName = typeName;
    if (internalContainerOf == null) {
      throw new NullPointerException("Null internalContainerOf");
    }
    this.internalContainerOf = internalContainerOf;
  }

  @Override
  public String typeName() {
    return typeName;
  }

  @Override
  ImmutableSet<String> internalContainerOf() {
    return internalContainerOf;
  }

  @Override
  public String toString() {
    return "AnnotationInfo{"
         + "typeName=" + typeName + ", "
         + "internalContainerOf=" + internalContainerOf
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof AnnotationInfo) {
      AnnotationInfo that = (AnnotationInfo) o;
      return (this.typeName.equals(that.typeName()))
           && (this.internalContainerOf.equals(that.internalContainerOf()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.typeName.hashCode();
    h *= 1000003;
    h ^= this.internalContainerOf.hashCode();
    return h;
  }

}
