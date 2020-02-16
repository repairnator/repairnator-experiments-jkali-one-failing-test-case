
package com.google.errorprone;

import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_CompositeCodeTransformer extends CompositeCodeTransformer {

  private final ImmutableList<CodeTransformer> transformers;

  AutoValue_CompositeCodeTransformer(
      ImmutableList<CodeTransformer> transformers) {
    if (transformers == null) {
      throw new NullPointerException("Null transformers");
    }
    this.transformers = transformers;
  }

  @Override
  public ImmutableList<CodeTransformer> transformers() {
    return transformers;
  }

  @Override
  public String toString() {
    return "CompositeCodeTransformer{"
         + "transformers=" + transformers
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof CompositeCodeTransformer) {
      CompositeCodeTransformer that = (CompositeCodeTransformer) o;
      return (this.transformers.equals(that.transformers()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.transformers.hashCode();
    return h;
  }

}
