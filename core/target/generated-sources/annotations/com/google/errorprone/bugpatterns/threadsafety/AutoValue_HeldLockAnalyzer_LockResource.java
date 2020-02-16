
package com.google.errorprone.bugpatterns.threadsafety;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_HeldLockAnalyzer_LockResource extends HeldLockAnalyzer.LockResource {

  private final String className;
  private final String lockMethod;
  private final String unlockMethod;

  AutoValue_HeldLockAnalyzer_LockResource(
      String className,
      String lockMethod,
      String unlockMethod) {
    if (className == null) {
      throw new NullPointerException("Null className");
    }
    this.className = className;
    if (lockMethod == null) {
      throw new NullPointerException("Null lockMethod");
    }
    this.lockMethod = lockMethod;
    if (unlockMethod == null) {
      throw new NullPointerException("Null unlockMethod");
    }
    this.unlockMethod = unlockMethod;
  }

  @Override
  String className() {
    return className;
  }

  @Override
  String lockMethod() {
    return lockMethod;
  }

  @Override
  String unlockMethod() {
    return unlockMethod;
  }

  @Override
  public String toString() {
    return "LockResource{"
         + "className=" + className + ", "
         + "lockMethod=" + lockMethod + ", "
         + "unlockMethod=" + unlockMethod
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof HeldLockAnalyzer.LockResource) {
      HeldLockAnalyzer.LockResource that = (HeldLockAnalyzer.LockResource) o;
      return (this.className.equals(that.className()))
           && (this.lockMethod.equals(that.lockMethod()))
           && (this.unlockMethod.equals(that.unlockMethod()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.className.hashCode();
    h *= 1000003;
    h ^= this.lockMethod.hashCode();
    h *= 1000003;
    h ^= this.unlockMethod.hashCode();
    return h;
  }

}
