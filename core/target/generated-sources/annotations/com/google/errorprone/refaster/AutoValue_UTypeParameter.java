
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UTypeParameter extends UTypeParameter {

  private final StringName name;
  private final ImmutableList<UExpression> bounds;
  private final ImmutableList<UAnnotation> annotations;

  AutoValue_UTypeParameter(
      StringName name,
      ImmutableList<UExpression> bounds,
      ImmutableList<UAnnotation> annotations) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    if (bounds == null) {
      throw new NullPointerException("Null bounds");
    }
    this.bounds = bounds;
    if (annotations == null) {
      throw new NullPointerException("Null annotations");
    }
    this.annotations = annotations;
  }

  @Override
  public StringName getName() {
    return name;
  }

  @Override
  public ImmutableList<UExpression> getBounds() {
    return bounds;
  }

  @Override
  public ImmutableList<UAnnotation> getAnnotations() {
    return annotations;
  }

  @Override
  public String toString() {
    return "UTypeParameter{"
         + "name=" + name + ", "
         + "bounds=" + bounds + ", "
         + "annotations=" + annotations
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UTypeParameter) {
      UTypeParameter that = (UTypeParameter) o;
      return (this.name.equals(that.getName()))
           && (this.bounds.equals(that.getBounds()))
           && (this.annotations.equals(that.getAnnotations()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.name.hashCode();
    h *= 1000003;
    h ^= this.bounds.hashCode();
    h *= 1000003;
    h ^= this.annotations.hashCode();
    return h;
  }

}
