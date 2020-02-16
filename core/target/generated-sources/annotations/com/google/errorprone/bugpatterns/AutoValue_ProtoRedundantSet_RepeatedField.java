
package com.google.errorprone.bugpatterns;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ProtoRedundantSet_RepeatedField extends ProtoRedundantSet.RepeatedField {

  private final String name;
  private final int index;

  AutoValue_ProtoRedundantSet_RepeatedField(
      String name,
      int index) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    this.index = index;
  }

  @Override
  String getName() {
    return name;
  }

  @Override
  int getIndex() {
    return index;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ProtoRedundantSet.RepeatedField) {
      ProtoRedundantSet.RepeatedField that = (ProtoRedundantSet.RepeatedField) o;
      return (this.name.equals(that.getName()))
           && (this.index == that.getIndex());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.name.hashCode();
    h *= 1000003;
    h ^= this.index;
    return h;
  }

}
