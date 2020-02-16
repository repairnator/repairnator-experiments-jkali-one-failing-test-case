
package com.google.errorprone.bugpatterns;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ProtoRedundantSet_SingleField extends ProtoRedundantSet.SingleField {

  private final String name;

  AutoValue_ProtoRedundantSet_SingleField(
      String name) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
  }

  @Override
  String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ProtoRedundantSet.SingleField) {
      ProtoRedundantSet.SingleField that = (ProtoRedundantSet.SingleField) o;
      return (this.name.equals(that.getName()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.name.hashCode();
    return h;
  }

}
