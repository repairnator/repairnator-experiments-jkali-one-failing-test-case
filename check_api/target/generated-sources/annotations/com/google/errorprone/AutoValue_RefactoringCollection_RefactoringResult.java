
package com.google.errorprone;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_RefactoringCollection_RefactoringResult extends RefactoringCollection.RefactoringResult {

  private final String message;
  private final RefactoringCollection.RefactoringResultType type;

  AutoValue_RefactoringCollection_RefactoringResult(
      String message,
      RefactoringCollection.RefactoringResultType type) {
    if (message == null) {
      throw new NullPointerException("Null message");
    }
    this.message = message;
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
  }

  @Override
  String message() {
    return message;
  }

  @Override
  RefactoringCollection.RefactoringResultType type() {
    return type;
  }

  @Override
  public String toString() {
    return "RefactoringResult{"
         + "message=" + message + ", "
         + "type=" + type
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof RefactoringCollection.RefactoringResult) {
      RefactoringCollection.RefactoringResult that = (RefactoringCollection.RefactoringResult) o;
      return (this.message.equals(that.message()))
           && (this.type.equals(that.type()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.message.hashCode();
    h *= 1000003;
    h ^= this.type.hashCode();
    return h;
  }

}
