
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UStaticIdent extends UStaticIdent {

  private final UClassIdent classIdent;
  private final StringName getName;
  private final UType memberType;

  AutoValue_UStaticIdent(
      UClassIdent classIdent,
      StringName getName,
      UType memberType) {
    if (classIdent == null) {
      throw new NullPointerException("Null classIdent");
    }
    this.classIdent = classIdent;
    if (getName == null) {
      throw new NullPointerException("Null getName");
    }
    this.getName = getName;
    if (memberType == null) {
      throw new NullPointerException("Null memberType");
    }
    this.memberType = memberType;
  }

  @Override
  UClassIdent classIdent() {
    return classIdent;
  }

  @Override
  public StringName getName() {
    return getName;
  }

  @Override
  UType memberType() {
    return memberType;
  }

  @Override
  public String toString() {
    return "UStaticIdent{"
         + "classIdent=" + classIdent + ", "
         + "getName=" + getName + ", "
         + "memberType=" + memberType
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UStaticIdent) {
      UStaticIdent that = (UStaticIdent) o;
      return (this.classIdent.equals(that.classIdent()))
           && (this.getName.equals(that.getName()))
           && (this.memberType.equals(that.memberType()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.classIdent.hashCode();
    h *= 1000003;
    h ^= this.getName.hashCode();
    h *= 1000003;
    h ^= this.memberType.hashCode();
    return h;
  }

}
