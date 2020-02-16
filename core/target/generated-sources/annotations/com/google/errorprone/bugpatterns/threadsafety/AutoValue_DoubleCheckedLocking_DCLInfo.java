
package com.google.errorprone.bugpatterns.threadsafety;

import com.sun.source.tree.IfTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.tools.javac.code.Symbol;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_DoubleCheckedLocking_DCLInfo extends DoubleCheckedLocking.DCLInfo {

  private final IfTree outerIf;
  private final SynchronizedTree synchTree;
  private final IfTree innerIf;
  private final Symbol.VarSymbol sym;

  AutoValue_DoubleCheckedLocking_DCLInfo(
      IfTree outerIf,
      SynchronizedTree synchTree,
      IfTree innerIf,
      Symbol.VarSymbol sym) {
    if (outerIf == null) {
      throw new NullPointerException("Null outerIf");
    }
    this.outerIf = outerIf;
    if (synchTree == null) {
      throw new NullPointerException("Null synchTree");
    }
    this.synchTree = synchTree;
    if (innerIf == null) {
      throw new NullPointerException("Null innerIf");
    }
    this.innerIf = innerIf;
    if (sym == null) {
      throw new NullPointerException("Null sym");
    }
    this.sym = sym;
  }

  @Override
  IfTree outerIf() {
    return outerIf;
  }

  @Override
  SynchronizedTree synchTree() {
    return synchTree;
  }

  @Override
  IfTree innerIf() {
    return innerIf;
  }

  @Override
  Symbol.VarSymbol sym() {
    return sym;
  }

  @Override
  public String toString() {
    return "DCLInfo{"
         + "outerIf=" + outerIf + ", "
         + "synchTree=" + synchTree + ", "
         + "innerIf=" + innerIf + ", "
         + "sym=" + sym
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof DoubleCheckedLocking.DCLInfo) {
      DoubleCheckedLocking.DCLInfo that = (DoubleCheckedLocking.DCLInfo) o;
      return (this.outerIf.equals(that.outerIf()))
           && (this.synchTree.equals(that.synchTree()))
           && (this.innerIf.equals(that.innerIf()))
           && (this.sym.equals(that.sym()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.outerIf.hashCode();
    h *= 1000003;
    h ^= this.synchTree.hashCode();
    h *= 1000003;
    h ^= this.innerIf.hashCode();
    h *= 1000003;
    h ^= this.sym.hashCode();
    return h;
  }

}
