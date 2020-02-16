
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UClassIdent extends UClassIdent {

  private final String topLevelClass;
  private final StringName name;

  AutoValue_UClassIdent(
      String topLevelClass,
      StringName name) {
    if (topLevelClass == null) {
      throw new NullPointerException("Null topLevelClass");
    }
    this.topLevelClass = topLevelClass;
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
  }

  @Override
  public String getTopLevelClass() {
    return topLevelClass;
  }

  @Override
  public StringName getName() {
    return name;
  }

  @Override
  public String toString() {
    return "UClassIdent{"
         + "topLevelClass=" + topLevelClass + ", "
         + "name=" + name
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UClassIdent) {
      UClassIdent that = (UClassIdent) o;
      return (this.topLevelClass.equals(that.getTopLevelClass()))
           && (this.name.equals(that.getName()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.topLevelClass.hashCode();
    h *= 1000003;
    h ^= this.name.hashCode();
    return h;
  }

}
