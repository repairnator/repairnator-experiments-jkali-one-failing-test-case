
package com.google.errorprone.bugpatterns;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ProtoRedundantSet_MapField extends ProtoRedundantSet.MapField {

  private final String name;
  private final Object key;

  AutoValue_ProtoRedundantSet_MapField(
      String name,
      Object key) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    if (key == null) {
      throw new NullPointerException("Null key");
    }
    this.key = key;
  }

  @Override
  String getName() {
    return name;
  }

  @Override
  Object getKey() {
    return key;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ProtoRedundantSet.MapField) {
      ProtoRedundantSet.MapField that = (ProtoRedundantSet.MapField) o;
      return (this.name.equals(that.getName()))
           && (this.key.equals(that.getKey()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.name.hashCode();
    h *= 1000003;
    h ^= this.key.hashCode();
    return h;
  }

}
