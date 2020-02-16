package io.leangen.graphql.metadata.strategy.query;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.leangen.graphql.util.ClassUtils;

/**
 * Created by bojan.tomic on 7/3/16.
 */
public class BeanOperationNameGenerator extends AnnotatedOperationNameGenerator {

    @Override
    public String generateQueryName(Method queryMethod, AnnotatedType declaringType, Object instance) {
        if (ClassUtils.isGetter(queryMethod)) {
            try {
                return super.generateQueryName(queryMethod, declaringType, instance);
            } catch (IllegalArgumentException e) {
                return ClassUtils.getFieldNameFromGetter(queryMethod);
            }
        }
        throw new IllegalArgumentException("Method " + queryMethod.toString() + " does not conform to the Java Bean specification");
    }

    @Override
    public String generateQueryName(Field queryField, AnnotatedType declaringType, Object instance) {
        try {
            return super.generateQueryName(queryField, declaringType, instance);
        } catch (IllegalArgumentException e) {
            return queryField.getName();
        }
    }

    @Override
    public String generateMutationName(Method mutationMethod, AnnotatedType declaringType, Object instance) {
        if (ClassUtils.isSetter(mutationMethod)) {
            try {
                return super.generateMutationName(mutationMethod, declaringType, instance);
            } catch (IllegalArgumentException e) {
                return ClassUtils.getFieldNameFromSetter(mutationMethod);
            }
        }
        throw new IllegalArgumentException("Method " + mutationMethod.toString() + " does not conform to the Java Bean specification");
    }
}
