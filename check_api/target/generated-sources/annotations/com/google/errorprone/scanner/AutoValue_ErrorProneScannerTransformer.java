
package com.google.errorprone.scanner;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ErrorProneScannerTransformer extends ErrorProneScannerTransformer {

  private final Scanner scanner;

  AutoValue_ErrorProneScannerTransformer(
      Scanner scanner) {
    if (scanner == null) {
      throw new NullPointerException("Null scanner");
    }
    this.scanner = scanner;
  }

  @Override
  Scanner scanner() {
    return scanner;
  }

  @Override
  public String toString() {
    return "ErrorProneScannerTransformer{"
         + "scanner=" + scanner
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ErrorProneScannerTransformer) {
      ErrorProneScannerTransformer that = (ErrorProneScannerTransformer) o;
      return (this.scanner.equals(that.scanner()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.scanner.hashCode();
    return h;
  }

}
