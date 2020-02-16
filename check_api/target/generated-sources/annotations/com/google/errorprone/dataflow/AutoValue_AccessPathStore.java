
package com.google.errorprone.dataflow;

import com.google.common.collect.ImmutableMap;
import javax.annotation.processing.Generated;
import org.checkerframework.dataflow.analysis.AbstractValue;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_AccessPathStore<V extends AbstractValue<V>> extends AccessPathStore<V> {

  private final ImmutableMap<AccessPath, V> heap;

  AutoValue_AccessPathStore(
      ImmutableMap<AccessPath, V> heap) {
    if (heap == null) {
      throw new NullPointerException("Null heap");
    }
    this.heap = heap;
  }

  @Override
  public ImmutableMap<AccessPath, V> heap() {
    return heap;
  }

  @Override
  public String toString() {
    return "AccessPathStore{"
         + "heap=" + heap
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof AccessPathStore) {
      AccessPathStore<?> that = (AccessPathStore<?>) o;
      return (this.heap.equals(that.heap()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.heap.hashCode();
    return h;
  }

}
