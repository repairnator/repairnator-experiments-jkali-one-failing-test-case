
package com.google.errorprone.refaster;

import com.google.errorprone.CodeTransformer;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_CodeTransformerTestHelper extends CodeTransformerTestHelper {

  private final CodeTransformer transformer;

  AutoValue_CodeTransformerTestHelper(
      CodeTransformer transformer) {
    if (transformer == null) {
      throw new NullPointerException("Null transformer");
    }
    this.transformer = transformer;
  }

  @Override
  CodeTransformer transformer() {
    return transformer;
  }

  @Override
  public String toString() {
    return "CodeTransformerTestHelper{"
         + "transformer=" + transformer
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof CodeTransformerTestHelper) {
      CodeTransformerTestHelper that = (CodeTransformerTestHelper) o;
      return (this.transformer.equals(that.transformer()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.transformer.hashCode();
    return h;
  }

}
