
package com.google.errorprone.dataflow;

import com.sun.source.util.TreePath;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_DataFlow_CfgParams extends DataFlow.CfgParams {

  private final TreePath methodPath;

  AutoValue_DataFlow_CfgParams(
      TreePath methodPath) {
    if (methodPath == null) {
      throw new NullPointerException("Null methodPath");
    }
    this.methodPath = methodPath;
  }

  @Override
  TreePath methodPath() {
    return methodPath;
  }

  @Override
  public String toString() {
    return "CfgParams{"
         + "methodPath=" + methodPath
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof DataFlow.CfgParams) {
      DataFlow.CfgParams that = (DataFlow.CfgParams) o;
      return (this.methodPath.equals(that.methodPath()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.methodPath.hashCode();
    return h;
  }

}
