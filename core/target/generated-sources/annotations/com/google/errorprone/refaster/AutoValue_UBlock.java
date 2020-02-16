
package com.google.errorprone.refaster;

import java.util.List;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UBlock extends UBlock {

  private final List<UStatement> statements;

  AutoValue_UBlock(
      List<UStatement> statements) {
    if (statements == null) {
      throw new NullPointerException("Null statements");
    }
    this.statements = statements;
  }

  @Override
  public List<UStatement> getStatements() {
    return statements;
  }

  @Override
  public String toString() {
    return "UBlock{"
         + "statements=" + statements
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UBlock) {
      UBlock that = (UBlock) o;
      return (this.statements.equals(that.getStatements()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.statements.hashCode();
    return h;
  }

}
