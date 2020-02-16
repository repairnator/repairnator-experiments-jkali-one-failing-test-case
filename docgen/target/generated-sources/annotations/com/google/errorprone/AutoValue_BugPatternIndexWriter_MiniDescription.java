
package com.google.errorprone;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_BugPatternIndexWriter_MiniDescription extends BugPatternIndexWriter.MiniDescription {

  private final String name;
  private final String summary;

  AutoValue_BugPatternIndexWriter_MiniDescription(
      String name,
      String summary) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    if (summary == null) {
      throw new NullPointerException("Null summary");
    }
    this.summary = summary;
  }

  @Override
  String name() {
    return name;
  }

  @Override
  String summary() {
    return summary;
  }

  @Override
  public String toString() {
    return "MiniDescription{"
         + "name=" + name + ", "
         + "summary=" + summary
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof BugPatternIndexWriter.MiniDescription) {
      BugPatternIndexWriter.MiniDescription that = (BugPatternIndexWriter.MiniDescription) o;
      return (this.name.equals(that.name()))
           && (this.summary.equals(that.summary()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.name.hashCode();
    h *= 1000003;
    h ^= this.summary.hashCode();
    return h;
  }

}
