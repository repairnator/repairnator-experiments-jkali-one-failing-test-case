package io.leangen.graphql.metadata.strategy.query;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by bojan.tomic on 7/12/16.
 */
public class DelegatingOperationNameGenerator implements OperationNameGenerator {

    private OperationNameGenerator[] delegateNameGenerators;

    @SuppressWarnings("WeakerAccess")
    public DelegatingOperationNameGenerator(OperationNameGenerator... delegateNameGenerators) {
        this.delegateNameGenerators = delegateNameGenerators;
    }

    @Override
    public String generateQueryName(Method queryMethod, AnnotatedType declaringType, Object instance) {
        Optional<String> queryName = generateName(queryNameGenerator -> queryNameGenerator.generateQueryName(queryMethod, declaringType, instance));
        return requireName(queryName, queryMethod);
    }

    @Override
    public String generateQueryName(Field queryField, AnnotatedType declaringType, Object instance) {
        Optional<String> queryName = generateName(queryNameGenerator -> queryNameGenerator.generateQueryName(queryField, declaringType, instance));
        return requireName(queryName, queryField);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private String requireName(Optional<String> queryName, Member query) {
        if (queryName.isPresent()) return queryName.get();

        throw new IllegalStateException(
                "Operation name impossible to determine from method " + query + " using the configured operation name generators: " + this.toString());
    }

    @Override
    public String generateMutationName(Method mutationMethod, AnnotatedType declaringType, Object instance) {
        Optional<String> mutationName = generateName(queryNameGenerator -> queryNameGenerator.generateMutationName(mutationMethod, declaringType, instance));
        return requireName(mutationName, mutationMethod);
    }

    @Override
    public String generateSubscriptionName(Method subscriptionMethod, AnnotatedType declaringType, Object instance) {
        Optional<String> subscriptionName = generateName(queryNameGenerator -> queryNameGenerator.generateSubscriptionName(subscriptionMethod, declaringType, instance));
        return requireName(subscriptionName, subscriptionMethod);
    }

    private Optional<String> generateName(Function<OperationNameGenerator, String> nameGeneratorFunction) {
        return Arrays.stream(delegateNameGenerators)
                .map(nameGeneratorFunction)
                .filter(queryName -> queryName != null && !queryName.isEmpty())
                .findFirst();
    }

    @Override
    public String toString() {
        StringBuilder nameGenerators = new StringBuilder(DelegatingOperationNameGenerator.class.getSimpleName());
        nameGenerators.append("[ ");
        Arrays.stream(delegateNameGenerators)
                .forEach(nameGen -> nameGenerators.append(nameGen.getClass().getSimpleName()).append(", "));
        nameGenerators.append("]");
        return nameGenerators.toString();
    }
}
