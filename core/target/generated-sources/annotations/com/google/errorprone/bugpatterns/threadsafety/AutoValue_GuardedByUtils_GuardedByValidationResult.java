
package com.google.errorprone.bugpatterns.threadsafety;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_GuardedByUtils_GuardedByValidationResult extends GuardedByUtils.GuardedByValidationResult {

  private final String message;
  private final Boolean isValid;

  AutoValue_GuardedByUtils_GuardedByValidationResult(
      String message,
      Boolean isValid) {
    if (message == null) {
      throw new NullPointerException("Null message");
    }
    this.message = message;
    if (isValid == null) {
      throw new NullPointerException("Null isValid");
    }
    this.isValid = isValid;
  }

  @Override
  String message() {
    return message;
  }

  @Override
  Boolean isValid() {
    return isValid;
  }

  @Override
  public String toString() {
    return "GuardedByValidationResult{"
         + "message=" + message + ", "
         + "isValid=" + isValid
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof GuardedByUtils.GuardedByValidationResult) {
      GuardedByUtils.GuardedByValidationResult that = (GuardedByUtils.GuardedByValidationResult) o;
      return (this.message.equals(that.message()))
           && (this.isValid.equals(that.isValid()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.message.hashCode();
    h *= 1000003;
    h ^= this.isValid.hashCode();
    return h;
  }

}
