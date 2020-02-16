
package com.google.errorprone.refaster;

import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UTry extends UTry {

  private final List<UTree<?>> resources;
  private final UBlock block;
  private final List<UCatch> catches;
  private final UBlock finallyBlock;

  AutoValue_UTry(
      List<UTree<?>> resources,
      UBlock block,
      List<UCatch> catches,
      @Nullable UBlock finallyBlock) {
    if (resources == null) {
      throw new NullPointerException("Null resources");
    }
    this.resources = resources;
    if (block == null) {
      throw new NullPointerException("Null block");
    }
    this.block = block;
    if (catches == null) {
      throw new NullPointerException("Null catches");
    }
    this.catches = catches;
    this.finallyBlock = finallyBlock;
  }

  @Override
  public List<UTree<?>> getResources() {
    return resources;
  }

  @Override
  public UBlock getBlock() {
    return block;
  }

  @Override
  public List<UCatch> getCatches() {
    return catches;
  }

  @Nullable
  @Override
  public UBlock getFinallyBlock() {
    return finallyBlock;
  }

  @Override
  public String toString() {
    return "UTry{"
         + "resources=" + resources + ", "
         + "block=" + block + ", "
         + "catches=" + catches + ", "
         + "finallyBlock=" + finallyBlock
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UTry) {
      UTry that = (UTry) o;
      return (this.resources.equals(that.getResources()))
           && (this.block.equals(that.getBlock()))
           && (this.catches.equals(that.getCatches()))
           && ((this.finallyBlock == null) ? (that.getFinallyBlock() == null) : this.finallyBlock.equals(that.getFinallyBlock()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.resources.hashCode();
    h *= 1000003;
    h ^= this.block.hashCode();
    h *= 1000003;
    h ^= this.catches.hashCode();
    h *= 1000003;
    h ^= (finallyBlock == null) ? 0 : this.finallyBlock.hashCode();
    return h;
  }

}
