
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_RefasterRule<M extends TemplateMatch, T extends Template<M>> extends RefasterRule<M, T> {

  private final String qualifiedTemplateClass;
  private final ImmutableList<UTypeVar> typeVariables;
  private final ImmutableList<T> beforeTemplates;
  private final ImmutableList<T> afterTemplates;
  private final ImmutableClassToInstanceMap<Annotation> annotations;

  AutoValue_RefasterRule(
      String qualifiedTemplateClass,
      ImmutableList<UTypeVar> typeVariables,
      ImmutableList<T> beforeTemplates,
      @Nullable ImmutableList<T> afterTemplates,
      ImmutableClassToInstanceMap<Annotation> annotations) {
    if (qualifiedTemplateClass == null) {
      throw new NullPointerException("Null qualifiedTemplateClass");
    }
    this.qualifiedTemplateClass = qualifiedTemplateClass;
    if (typeVariables == null) {
      throw new NullPointerException("Null typeVariables");
    }
    this.typeVariables = typeVariables;
    if (beforeTemplates == null) {
      throw new NullPointerException("Null beforeTemplates");
    }
    this.beforeTemplates = beforeTemplates;
    this.afterTemplates = afterTemplates;
    if (annotations == null) {
      throw new NullPointerException("Null annotations");
    }
    this.annotations = annotations;
  }

  @Override
  String qualifiedTemplateClass() {
    return qualifiedTemplateClass;
  }

  @Override
  ImmutableList<UTypeVar> typeVariables() {
    return typeVariables;
  }

  @Override
  ImmutableList<T> beforeTemplates() {
    return beforeTemplates;
  }

  @Nullable
  @Override
  ImmutableList<T> afterTemplates() {
    return afterTemplates;
  }

  @Override
  public ImmutableClassToInstanceMap<Annotation> annotations() {
    return annotations;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof RefasterRule) {
      RefasterRule<?, ?> that = (RefasterRule<?, ?>) o;
      return (this.qualifiedTemplateClass.equals(that.qualifiedTemplateClass()))
           && (this.typeVariables.equals(that.typeVariables()))
           && (this.beforeTemplates.equals(that.beforeTemplates()))
           && ((this.afterTemplates == null) ? (that.afterTemplates() == null) : this.afterTemplates.equals(that.afterTemplates()))
           && (this.annotations.equals(that.annotations()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.qualifiedTemplateClass.hashCode();
    h *= 1000003;
    h ^= this.typeVariables.hashCode();
    h *= 1000003;
    h ^= this.beforeTemplates.hashCode();
    h *= 1000003;
    h ^= (afterTemplates == null) ? 0 : this.afterTemplates.hashCode();
    h *= 1000003;
    h ^= this.annotations.hashCode();
    return h;
  }

}
