
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableList;
import com.sun.source.tree.MemberReferenceTree;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_UMemberReference extends UMemberReference {

  private final MemberReferenceTree.ReferenceMode mode;
  private final UExpression qualifierExpression;
  private final StringName name;
  private final ImmutableList<UExpression> typeArguments;

  AutoValue_UMemberReference(
      MemberReferenceTree.ReferenceMode mode,
      UExpression qualifierExpression,
      StringName name,
      @Nullable ImmutableList<UExpression> typeArguments) {
    if (mode == null) {
      throw new NullPointerException("Null mode");
    }
    this.mode = mode;
    if (qualifierExpression == null) {
      throw new NullPointerException("Null qualifierExpression");
    }
    this.qualifierExpression = qualifierExpression;
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    this.typeArguments = typeArguments;
  }

  @Override
  public MemberReferenceTree.ReferenceMode getMode() {
    return mode;
  }

  @Override
  public UExpression getQualifierExpression() {
    return qualifierExpression;
  }

  @Override
  public StringName getName() {
    return name;
  }

  @Nullable
  @Override
  public ImmutableList<UExpression> getTypeArguments() {
    return typeArguments;
  }

  @Override
  public String toString() {
    return "UMemberReference{"
         + "mode=" + mode + ", "
         + "qualifierExpression=" + qualifierExpression + ", "
         + "name=" + name + ", "
         + "typeArguments=" + typeArguments
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UMemberReference) {
      UMemberReference that = (UMemberReference) o;
      return (this.mode.equals(that.getMode()))
           && (this.qualifierExpression.equals(that.getQualifierExpression()))
           && (this.name.equals(that.getName()))
           && ((this.typeArguments == null) ? (that.getTypeArguments() == null) : this.typeArguments.equals(that.getTypeArguments()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.mode.hashCode();
    h *= 1000003;
    h ^= this.qualifierExpression.hashCode();
    h *= 1000003;
    h ^= this.name.hashCode();
    h *= 1000003;
    h ^= (typeArguments == null) ? 0 : this.typeArguments.hashCode();
    return h;
  }

}
