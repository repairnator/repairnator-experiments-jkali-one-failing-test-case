
package com.google.errorprone;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_BugPatternIndexWriter_IndexEntry extends BugPatternIndexWriter.IndexEntry {

  private final boolean onByDefault;
  private final BugPattern.SeverityLevel severity;

  AutoValue_BugPatternIndexWriter_IndexEntry(
      boolean onByDefault,
      BugPattern.SeverityLevel severity) {
    this.onByDefault = onByDefault;
    if (severity == null) {
      throw new NullPointerException("Null severity");
    }
    this.severity = severity;
  }

  @Override
  boolean onByDefault() {
    return onByDefault;
  }

  @Override
  BugPattern.SeverityLevel severity() {
    return severity;
  }

  @Override
  public String toString() {
    return "IndexEntry{"
         + "onByDefault=" + onByDefault + ", "
         + "severity=" + severity
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof BugPatternIndexWriter.IndexEntry) {
      BugPatternIndexWriter.IndexEntry that = (BugPatternIndexWriter.IndexEntry) o;
      return (this.onByDefault == that.onByDefault())
           && (this.severity.equals(that.severity()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.onByDefault ? 1231 : 1237;
    h *= 1000003;
    h ^= this.severity.hashCode();
    return h;
  }

}
