
package com.google.errorprone.refaster;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ULabeledStatement extends ULabeledStatement {

  private final StringName label;
  private final USimpleStatement statement;

  AutoValue_ULabeledStatement(
      StringName label,
      USimpleStatement statement) {
    if (label == null) {
      throw new NullPointerException("Null label");
    }
    this.label = label;
    if (statement == null) {
      throw new NullPointerException("Null statement");
    }
    this.statement = statement;
  }

  @Override
  public StringName getLabel() {
    return label;
  }

  @Override
  public USimpleStatement getStatement() {
    return statement;
  }

  @Override
  public String toString() {
    return "ULabeledStatement{"
         + "label=" + label + ", "
         + "statement=" + statement
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ULabeledStatement) {
      ULabeledStatement that = (ULabeledStatement) o;
      return (this.label.equals(that.getLabel()))
           && (this.statement.equals(that.getStatement()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.label.hashCode();
    h *= 1000003;
    h ^= this.statement.hashCode();
    return h;
  }

}
