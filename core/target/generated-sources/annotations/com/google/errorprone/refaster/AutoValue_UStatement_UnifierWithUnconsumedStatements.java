
package com.google.errorprone.refaster;

import com.sun.source.tree.StatementTree;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UStatement_UnifierWithUnconsumedStatements extends UStatement.UnifierWithUnconsumedStatements {

  private final Unifier unifier;
  private final List<? extends StatementTree> unconsumedStatements;

  AutoValue_UStatement_UnifierWithUnconsumedStatements(
      Unifier unifier,
      List<? extends StatementTree> unconsumedStatements) {
    if (unifier == null) {
      throw new NullPointerException("Null unifier");
    }
    this.unifier = unifier;
    if (unconsumedStatements == null) {
      throw new NullPointerException("Null unconsumedStatements");
    }
    this.unconsumedStatements = unconsumedStatements;
  }

  @Override
  public Unifier unifier() {
    return unifier;
  }

  @Override
  public List<? extends StatementTree> unconsumedStatements() {
    return unconsumedStatements;
  }

  @Override
  public String toString() {
    return "UnifierWithUnconsumedStatements{"
         + "unifier=" + unifier + ", "
         + "unconsumedStatements=" + unconsumedStatements
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UStatement.UnifierWithUnconsumedStatements) {
      UStatement.UnifierWithUnconsumedStatements that = (UStatement.UnifierWithUnconsumedStatements) o;
      return (this.unifier.equals(that.unifier()))
           && (this.unconsumedStatements.equals(that.unconsumedStatements()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.unifier.hashCode();
    h *= 1000003;
    h ^= this.unconsumedStatements.hashCode();
    return h;
  }

}
