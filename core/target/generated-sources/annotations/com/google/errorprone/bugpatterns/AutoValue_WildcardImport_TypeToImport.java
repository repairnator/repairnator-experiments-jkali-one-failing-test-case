
package com.google.errorprone.bugpatterns;

import com.sun.tools.javac.code.Symbol;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_WildcardImport_TypeToImport extends WildcardImport.TypeToImport {

  private final String name;
  private final Symbol owner;
  private final boolean isStatic;

  AutoValue_WildcardImport_TypeToImport(
      String name,
      Symbol owner,
      boolean isStatic) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    if (owner == null) {
      throw new NullPointerException("Null owner");
    }
    this.owner = owner;
    this.isStatic = isStatic;
  }

  @Override
  String name() {
    return name;
  }

  @Override
  Symbol owner() {
    return owner;
  }

  @Override
  boolean isStatic() {
    return isStatic;
  }

  @Override
  public String toString() {
    return "TypeToImport{"
         + "name=" + name + ", "
         + "owner=" + owner + ", "
         + "isStatic=" + isStatic
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof WildcardImport.TypeToImport) {
      WildcardImport.TypeToImport that = (WildcardImport.TypeToImport) o;
      return (this.name.equals(that.name()))
           && (this.owner.equals(that.owner()))
           && (this.isStatic == that.isStatic());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.name.hashCode();
    h *= 1000003;
    h ^= this.owner.hashCode();
    h *= 1000003;
    h ^= this.isStatic ? 1231 : 1237;
    return h;
  }

}
