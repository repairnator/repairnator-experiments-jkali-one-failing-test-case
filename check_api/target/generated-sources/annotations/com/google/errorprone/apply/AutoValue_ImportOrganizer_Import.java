
package com.google.errorprone.apply;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ImportOrganizer_Import extends ImportOrganizer.Import {

  private final boolean static0;
  private final String type;

  AutoValue_ImportOrganizer_Import(
      boolean static0,
      String type) {
    this.static0 = static0;
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
  }

  @Override
  public boolean isStatic() {
    return static0;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ImportOrganizer.Import) {
      ImportOrganizer.Import that = (ImportOrganizer.Import) o;
      return (this.static0 == that.isStatic())
           && (this.type.equals(that.getType()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.static0 ? 1231 : 1237;
    h *= 1000003;
    h ^= this.type.hashCode();
    return h;
  }

}
