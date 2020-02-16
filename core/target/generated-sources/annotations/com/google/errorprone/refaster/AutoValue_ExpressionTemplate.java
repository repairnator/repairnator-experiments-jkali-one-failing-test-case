
package com.google.errorprone.refaster;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.lang.annotation.Annotation;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ExpressionTemplate extends ExpressionTemplate {

  private final ImmutableClassToInstanceMap<Annotation> annotations;
  private final ImmutableList<UTypeVar> templateTypeVariables;
  private final ImmutableMap<String, UType> expressionArgumentTypes;
  private final UExpression expression;
  private final UType returnType;

  AutoValue_ExpressionTemplate(
      ImmutableClassToInstanceMap<Annotation> annotations,
      ImmutableList<UTypeVar> templateTypeVariables,
      ImmutableMap<String, UType> expressionArgumentTypes,
      UExpression expression,
      UType returnType) {
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
    if (expression == null) {
      throw new NullPointerException("Null expression");
    }
    this.expression = expression;
    if (returnType == null) {
      throw new NullPointerException("Null returnType");
    }
    this.returnType = returnType;
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
  UExpression expression() {
    return expression;
  }

  @Override
  UType returnType() {
    return returnType;
  }

  @Override
  public String toString() {
    return "ExpressionTemplate{"
         + "annotations=" + annotations + ", "
         + "templateTypeVariables=" + templateTypeVariables + ", "
         + "expressionArgumentTypes=" + expressionArgumentTypes + ", "
         + "expression=" + expression + ", "
         + "returnType=" + returnType
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ExpressionTemplate) {
      ExpressionTemplate that = (ExpressionTemplate) o;
      return (this.annotations.equals(that.annotations()))
           && (this.templateTypeVariables.equals(that.templateTypeVariables()))
           && (this.expressionArgumentTypes.equals(that.expressionArgumentTypes()))
           && (this.expression.equals(that.expression()))
           && (this.returnType.equals(that.returnType()));
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
    h ^= this.expression.hashCode();
    h *= 1000003;
    h ^= this.returnType.hashCode();
    return h;
  }

}
