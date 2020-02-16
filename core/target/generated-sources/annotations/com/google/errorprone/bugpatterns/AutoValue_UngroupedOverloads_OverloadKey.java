
package com.google.errorprone.bugpatterns;

import javax.annotation.processing.Generated;
import javax.lang.model.element.Name;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UngroupedOverloads_OverloadKey extends UngroupedOverloads.OverloadKey {

  private final Name name;

  AutoValue_UngroupedOverloads_OverloadKey(
      Name name) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
  }

  @Override
  Name name() {
    return name;
  }

  @Override
  public String toString() {
    return "OverloadKey{"
         + "name=" + name
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UngroupedOverloads.OverloadKey) {
      UngroupedOverloads.OverloadKey that = (UngroupedOverloads.OverloadKey) o;
      return (this.name.equals(that.name()));
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
