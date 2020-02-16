
package com.google.errorprone.dataflow;

import javax.annotation.processing.Generated;
import org.checkerframework.dataflow.analysis.TransferFunction;
import org.checkerframework.dataflow.cfg.ControlFlowGraph;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_DataFlow_AnalysisParams extends DataFlow.AnalysisParams {

  private final TransferFunction<?, ?> transferFunction;
  private final ControlFlowGraph cfg;

  AutoValue_DataFlow_AnalysisParams(
      TransferFunction<?, ?> transferFunction,
      ControlFlowGraph cfg) {
    if (transferFunction == null) {
      throw new NullPointerException("Null transferFunction");
    }
    this.transferFunction = transferFunction;
    if (cfg == null) {
      throw new NullPointerException("Null cfg");
    }
    this.cfg = cfg;
  }

  @Override
  TransferFunction<?, ?> transferFunction() {
    return transferFunction;
  }

  @Override
  ControlFlowGraph cfg() {
    return cfg;
  }

  @Override
  public String toString() {
    return "AnalysisParams{"
         + "transferFunction=" + transferFunction + ", "
         + "cfg=" + cfg
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof DataFlow.AnalysisParams) {
      DataFlow.AnalysisParams that = (DataFlow.AnalysisParams) o;
      return (this.transferFunction.equals(that.transferFunction()))
           && (this.cfg.equals(that.cfg()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.transferFunction.hashCode();
    h *= 1000003;
    h ^= this.cfg.hashCode();
    return h;
  }

}
