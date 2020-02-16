
package com.google.errorprone.refaster;

import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UBreak extends UBreak {

  private final StringName label;

  AutoValue_UBreak(
      @Nullable StringName label) {
    this.label = label;
  }

  @Nullable
  @Override
  public StringName getLabel() {
    return label;
  }

  @Override
  public String toString() {
    return "UBreak{"
         + "label=" + label
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UBreak) {
      UBreak that = (UBreak) o;
      return ((this.label == null) ? (that.getLabel() == null) : this.label.equals(that.getLabel()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (label == null) ? 0 : this.label.hashCode();
    return h;
  }

}
