
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UCatch extends UCatch {

  private final UVariableDecl parameter;
  private final UBlock block;

  AutoValue_UCatch(
      UVariableDecl parameter,
      UBlock block) {
    if (parameter == null) {
      throw new NullPointerException("Null parameter");
    }
    this.parameter = parameter;
    if (block == null) {
      throw new NullPointerException("Null block");
    }
    this.block = block;
  }

  @Override
  public UVariableDecl getParameter() {
    return parameter;
  }

  @Override
  public UBlock getBlock() {
    return block;
  }

  @Override
  public String toString() {
    return "UCatch{"
         + "parameter=" + parameter + ", "
         + "block=" + block
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UCatch) {
      UCatch that = (UCatch) o;
      return (this.parameter.equals(that.getParameter()))
           && (this.block.equals(that.getBlock()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.parameter.hashCode();
    h *= 1000003;
    h ^= this.block.hashCode();
    return h;
  }

}
