
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UFreeIdent extends UFreeIdent {

  private final StringName name;

  AutoValue_UFreeIdent(
      StringName name) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
  }

  @Override
  public StringName getName() {
    return name;
  }

  @Override
  public String toString() {
    return "UFreeIdent{"
         + "name=" + name
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UFreeIdent) {
      UFreeIdent that = (UFreeIdent) o;
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
