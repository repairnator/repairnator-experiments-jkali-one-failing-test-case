
package com.google.errorprone.refaster;

import com.sun.source.tree.Tree;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UBinary extends UBinary {

  private final Tree.Kind kind;
  private final UExpression leftOperand;
  private final UExpression rightOperand;

  AutoValue_UBinary(
      Tree.Kind kind,
      UExpression leftOperand,
      UExpression rightOperand) {
    if (kind == null) {
      throw new NullPointerException("Null kind");
    }
    this.kind = kind;
    if (leftOperand == null) {
      throw new NullPointerException("Null leftOperand");
    }
    this.leftOperand = leftOperand;
    if (rightOperand == null) {
      throw new NullPointerException("Null rightOperand");
    }
    this.rightOperand = rightOperand;
  }

  @Override
  public Tree.Kind getKind() {
    return kind;
  }

  @Override
  public UExpression getLeftOperand() {
    return leftOperand;
  }

  @Override
  public UExpression getRightOperand() {
    return rightOperand;
  }

  @Override
  public String toString() {
    return "UBinary{"
         + "kind=" + kind + ", "
         + "leftOperand=" + leftOperand + ", "
         + "rightOperand=" + rightOperand
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UBinary) {
      UBinary that = (UBinary) o;
      return (this.kind.equals(that.getKind()))
           && (this.leftOperand.equals(that.getLeftOperand()))
           && (this.rightOperand.equals(that.getRightOperand()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.kind.hashCode();
    h *= 1000003;
    h ^= this.leftOperand.hashCode();
    h *= 1000003;
    h ^= this.rightOperand.hashCode();
    return h;
  }

}
