
package com.google.errorprone.refaster;

import java.util.Map;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_Match extends Match {

  private final Map<String, String> bindings;

  AutoValue_Match(
      Map<String, String> bindings) {
    if (bindings == null) {
      throw new NullPointerException("Null bindings");
    }
    this.bindings = bindings;
  }

  @Override
  Map<String, String> bindings() {
    return bindings;
  }

  @Override
  public String toString() {
    return "Match{"
         + "bindings=" + bindings
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Match) {
      Match that = (Match) o;
      return (this.bindings.equals(that.bindings()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.bindings.hashCode();
    return h;
  }

}
