
package com.google.errorprone.refaster;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UPlaceholderStatement_ConsumptionState extends UPlaceholderStatement.ConsumptionState {

  private final int consumedStatements;
  private final List<JCTree.JCStatement> placeholderImplInReverseOrder;

  AutoValue_UPlaceholderStatement_ConsumptionState(
      int consumedStatements,
      List<JCTree.JCStatement> placeholderImplInReverseOrder) {
    this.consumedStatements = consumedStatements;
    if (placeholderImplInReverseOrder == null) {
      throw new NullPointerException("Null placeholderImplInReverseOrder");
    }
    this.placeholderImplInReverseOrder = placeholderImplInReverseOrder;
  }

  @Override
  int consumedStatements() {
    return consumedStatements;
  }

  @Override
  List<JCTree.JCStatement> placeholderImplInReverseOrder() {
    return placeholderImplInReverseOrder;
  }

  @Override
  public String toString() {
    return "ConsumptionState{"
         + "consumedStatements=" + consumedStatements + ", "
         + "placeholderImplInReverseOrder=" + placeholderImplInReverseOrder
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UPlaceholderStatement.ConsumptionState) {
      UPlaceholderStatement.ConsumptionState that = (UPlaceholderStatement.ConsumptionState) o;
      return (this.consumedStatements == that.consumedStatements())
           && (this.placeholderImplInReverseOrder.equals(that.placeholderImplInReverseOrder()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.consumedStatements;
    h *= 1000003;
    h ^= this.placeholderImplInReverseOrder.hashCode();
    return h;
  }

}
