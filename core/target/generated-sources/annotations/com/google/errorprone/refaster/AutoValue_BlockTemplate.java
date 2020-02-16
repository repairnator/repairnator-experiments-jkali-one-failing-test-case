
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.lang.annotation.Annotation;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_BlockTemplate extends BlockTemplate {

  private final ImmutableClassToInstanceMap<Annotation> annotations;
  private final ImmutableList<UTypeVar> templateTypeVariables;
  private final ImmutableMap<String, UType> expressionArgumentTypes;
  private final ImmutableList<UStatement> templateStatements;

  AutoValue_BlockTemplate(
      ImmutableClassToInstanceMap<Annotation> annotations,
      ImmutableList<UTypeVar> templateTypeVariables,
      ImmutableMap<String, UType> expressionArgumentTypes,
      ImmutableList<UStatement> templateStatements) {
    if (annotations == null) {
      throw new NullPointerException("Null annotations");
    }
    this.annotations = annotations;
    if (templateTypeVariables == null) {
      throw new NullPointerException("Null templateTypeVariables");
    }
    this.templateTypeVariables = templateTypeVariables;
    if (expressionArgumentTypes == null) {
      throw new NullPointerException("Null expressionArgumentTypes");
    }
    this.expressionArgumentTypes = expressionArgumentTypes;
    if (templateStatements == null) {
      throw new NullPointerException("Null templateStatements");
    }
    this.templateStatements = templateStatements;
  }

  @Override
  public ImmutableClassToInstanceMap<Annotation> annotations() {
    return annotations;
  }

  @Override
  public ImmutableList<UTypeVar> templateTypeVariables() {
    return templateTypeVariables;
  }

  @Override
  public ImmutableMap<String, UType> expressionArgumentTypes() {
    return expressionArgumentTypes;
  }

  @Override
  ImmutableList<UStatement> templateStatements() {
    return templateStatements;
  }

  @Override
  public String toString() {
    return "BlockTemplate{"
         + "annotations=" + annotations + ", "
         + "templateTypeVariables=" + templateTypeVariables + ", "
         + "expressionArgumentTypes=" + expressionArgumentTypes + ", "
         + "templateStatements=" + templateStatements
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof BlockTemplate) {
      BlockTemplate that = (BlockTemplate) o;
      return (this.annotations.equals(that.annotations()))
           && (this.templateTypeVariables.equals(that.templateTypeVariables()))
           && (this.expressionArgumentTypes.equals(that.expressionArgumentTypes()))
           && (this.templateStatements.equals(that.templateStatements()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.annotations.hashCode();
    h *= 1000003;
    h ^= this.templateTypeVariables.hashCode();
    h *= 1000003;
    h ^= this.expressionArgumentTypes.hashCode();
    h *= 1000003;
    h ^= this.templateStatements.hashCode();
    return h;
  }

}
