
package com.google.errorprone.refaster;

import com.google.errorprone.matchers.Matcher;
import com.sun.source.tree.ExpressionTree;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UMatches extends UMatches {

  private final boolean positive;
  private final Class<? extends Matcher<? super ExpressionTree>> matcherClass;
  private final UExpression expression;

  AutoValue_UMatches(
      boolean positive,
      Class<? extends Matcher<? super ExpressionTree>> matcherClass,
      UExpression expression) {
    this.positive = positive;
    if (matcherClass == null) {
      throw new NullPointerException("Null matcherClass");
    }
    this.matcherClass = matcherClass;
    if (expression == null) {
      throw new NullPointerException("Null expression");
    }
    this.expression = expression;
  }

  @Override
  boolean positive() {
    return positive;
  }

  @Override
  Class<? extends Matcher<? super ExpressionTree>> matcherClass() {
    return matcherClass;
  }

  @Override
  UExpression expression() {
    return expression;
  }

  @Override
  public String toString() {
    return "UMatches{"
         + "positive=" + positive + ", "
         + "matcherClass=" + matcherClass + ", "
         + "expression=" + expression
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UMatches) {
      UMatches that = (UMatches) o;
      return (this.positive == that.positive())
           && (this.matcherClass.equals(that.matcherClass()))
           && (this.expression.equals(that.expression()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.positive ? 1231 : 1237;
    h *= 1000003;
    h ^= this.matcherClass.hashCode();
    h *= 1000003;
    h ^= this.expression.hashCode();
    return h;
  }

}
