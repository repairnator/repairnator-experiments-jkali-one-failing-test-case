
package com.google.errorprone.bugpatterns.formatstring;

import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_FormatStringValidation_ValidationResult extends FormatStringValidation.ValidationResult {

  private final Exception exception;
  private final String message;

  AutoValue_FormatStringValidation_ValidationResult(
      @Nullable Exception exception,
      String message) {
    this.exception = exception;
    if (message == null) {
      throw new NullPointerException("Null message");
    }
    this.message = message;
  }

  @Nullable
  @Override
  public Exception exception() {
    return exception;
  }

  @Override
  public String message() {
    return message;
  }

  @Override
  public String toString() {
    return "ValidationResult{"
         + "exception=" + exception + ", "
         + "message=" + message
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof FormatStringValidation.ValidationResult) {
      FormatStringValidation.ValidationResult that = (FormatStringValidation.ValidationResult) o;
      return ((this.exception == null) ? (that.exception() == null) : this.exception.equals(that.exception()))
           && (this.message.equals(that.message()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (exception == null) ? 0 : this.exception.hashCode();
    h *= 1000003;
    h ^= this.message.hashCode();
    return h;
  }

}
