
package com.google.errorprone.refaster;

import com.google.errorprone.DescriptionListener;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_RefasterScanner<M extends TemplateMatch, T extends Template<M>> extends RefasterScanner<M, T> {

  private final RefasterRule<M, T> rule;
  private final DescriptionListener listener;

  AutoValue_RefasterScanner(
      RefasterRule<M, T> rule,
      DescriptionListener listener) {
    if (rule == null) {
      throw new NullPointerException("Null rule");
    }
    this.rule = rule;
    if (listener == null) {
      throw new NullPointerException("Null listener");
    }
    this.listener = listener;
  }

  @Override
  RefasterRule<M, T> rule() {
    return rule;
  }

  @Override
  DescriptionListener listener() {
    return listener;
  }

  @Override
  public String toString() {
    return "RefasterScanner{"
         + "rule=" + rule + ", "
         + "listener=" + listener
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof RefasterScanner) {
      RefasterScanner<?, ?> that = (RefasterScanner<?, ?>) o;
      return (this.rule.equals(that.rule()))
           && (this.listener.equals(that.listener()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.rule.hashCode();
    h *= 1000003;
    h ^= this.listener.hashCode();
    return h;
  }

}
