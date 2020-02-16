
package com.google.errorprone.bugpatterns.argumentselectiondefects;

import com.sun.tools.javac.parser.Tokens;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_NamedParameterComment_MatchedComment extends NamedParameterComment.MatchedComment {

  private final Tokens.Comment comment;
  private final NamedParameterComment.MatchType matchType;

  AutoValue_NamedParameterComment_MatchedComment(
      Tokens.Comment comment,
      NamedParameterComment.MatchType matchType) {
    if (comment == null) {
      throw new NullPointerException("Null comment");
    }
    this.comment = comment;
    if (matchType == null) {
      throw new NullPointerException("Null matchType");
    }
    this.matchType = matchType;
  }

  @Override
  Tokens.Comment comment() {
    return comment;
  }

  @Override
  NamedParameterComment.MatchType matchType() {
    return matchType;
  }

  @Override
  public String toString() {
    return "MatchedComment{"
         + "comment=" + comment + ", "
         + "matchType=" + matchType
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof NamedParameterComment.MatchedComment) {
      NamedParameterComment.MatchedComment that = (NamedParameterComment.MatchedComment) o;
      return (this.comment.equals(that.comment()))
           && (this.matchType.equals(that.matchType()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.comment.hashCode();
    h *= 1000003;
    h ^= this.matchType.hashCode();
    return h;
  }

}
