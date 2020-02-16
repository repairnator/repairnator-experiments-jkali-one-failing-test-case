
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_StringName extends StringName {

  private final String contents;

  AutoValue_StringName(
      String contents) {
    if (contents == null) {
      throw new NullPointerException("Null contents");
    }
    this.contents = contents;
  }

  @Override
  String contents() {
    return contents;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof StringName) {
      StringName that = (StringName) o;
      return (this.contents.equals(that.contents()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.contents.hashCode();
    return h;
  }

}
