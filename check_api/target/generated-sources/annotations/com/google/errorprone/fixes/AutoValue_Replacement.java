
package com.google.errorprone.fixes;

import com.google.common.collect.Range;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_Replacement extends Replacement {

  private final Range<Integer> range;
  private final String replaceWith;

  AutoValue_Replacement(
      Range<Integer> range,
      String replaceWith) {
    if (range == null) {
      throw new NullPointerException("Null range");
    }
    this.range = range;
    if (replaceWith == null) {
      throw new NullPointerException("Null replaceWith");
    }
    this.replaceWith = replaceWith;
  }

  @Override
  public Range<Integer> range() {
    return range;
  }

  @Override
  public String replaceWith() {
    return replaceWith;
  }

  @Override
  public String toString() {
    return "Replacement{"
         + "range=" + range + ", "
         + "replaceWith=" + replaceWith
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Replacement) {
      Replacement that = (Replacement) o;
      return (this.range.equals(that.range()))
           && (this.replaceWith.equals(that.replaceWith()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.range.hashCode();
    h *= 1000003;
    h ^= this.replaceWith.hashCode();
    return h;
  }

}
