
package com.google.errorprone.bugpatterns.threadsafety;

import javax.annotation.processing.Generated;
import org.pcollections.ConsPStack;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ThreadSafety_Violation extends ThreadSafety.Violation {

  private final ConsPStack<String> path;

  AutoValue_ThreadSafety_Violation(
      ConsPStack<String> path) {
    if (path == null) {
      throw new NullPointerException("Null path");
    }
    this.path = path;
  }

  @Override
  public ConsPStack<String> path() {
    return path;
  }

  @Override
  public String toString() {
    return "Violation{"
         + "path=" + path
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ThreadSafety.Violation) {
      ThreadSafety.Violation that = (ThreadSafety.Violation) o;
      return (this.path.equals(that.path()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.path.hashCode();
    return h;
  }

}
