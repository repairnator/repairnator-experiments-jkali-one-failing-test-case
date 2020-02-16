
package com.google.errorprone.refaster;

import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UBlank extends UBlank {

  private final UUID unique;

  AutoValue_UBlank(
      UUID unique) {
    if (unique == null) {
      throw new NullPointerException("Null unique");
    }
    this.unique = unique;
  }

  @Override
  UUID unique() {
    return unique;
  }

  @Override
  public String toString() {
    return "UBlank{"
         + "unique=" + unique
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UBlank) {
      UBlank that = (UBlank) o;
      return (this.unique.equals(that.unique()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.unique.hashCode();
    return h;
  }

}
