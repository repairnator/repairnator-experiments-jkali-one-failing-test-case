package io.leangen.graphql.generator;

import java.lang.reflect.AnnotatedType;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLModifiedType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import io.leangen.graphql.execution.GlobalEnvironment;
import io.leangen.graphql.generator.mapping.TypeMapperRepository;
import io.leangen.graphql.generator.types.MappedGraphQLType;
import io.leangen.graphql.metadata.strategy.type.TypeInfoGenerator;
import io.leangen.graphql.util.ClassUtils;
import io.leangen.graphql.util.Urls;

class Validator {

    private final GlobalEnvironment environment;
    private final TypeMapperRepository mappers;
    private final Map<String, AnnotatedType> mappedTypes;
    private final Map<String, AnnotatedType> mappedInputTypes;

    Validator(GlobalEnvironment environment, TypeMapperRepository mappers, Set<GraphQLType> knownTypes) {
        this.environment = environment;
        this.mappers = mappers;
        this.mappedTypes = knownTypes.stream()
                .filter(type -> type instanceof GraphQLOutputType && type instanceof MappedGraphQLType)
                .collect(Collectors.toMap(GraphQLType::getName, type -> ClassUtils.normalize(((MappedGraphQLType) type).getJavaType())));
        this.mappedInputTypes = knownTypes.stream()
                .filter(type -> type instanceof GraphQLInputType && type instanceof MappedGraphQLType)
                .collect(Collectors.toMap(GraphQLType::getName, type -> ClassUtils.normalize(((MappedGraphQLType) type).getJavaType())));
    }

    ValidationResult checkUniqueness(GraphQLOutputType graphQLType, AnnotatedType javaType) {
        return checkUniqueness(graphQLType, () -> mappers.getMappableOutputType(javaType), mappedTypes);
    }

    ValidationResult checkUniqueness(GraphQLInputType graphQLType, AnnotatedType javaType) {
        return checkUniqueness(graphQLType, () -> environment.getMappableInputType(javaType), mappedInputTypes);
    }

    private ValidationResult checkUniqueness(GraphQLType graphQLType, Supplier<AnnotatedType> javaType, Map<String, AnnotatedType> known) {
        if (graphQLType instanceof GraphQLModifiedType) {
            return ValidationResult.valid();
        }
        AnnotatedType resolvedType;
        try {
            resolvedType = resolveType(graphQLType, javaType);
        } catch (Exception e) {
            return ValidationResult.invalid(
                    String.format("Exception while checking the name uniqueness for %s: %s", graphQLType.getName(), e.getMessage()));
        }
        known.putIfAbsent(graphQLType.getName(), resolvedType);
        AnnotatedType knownType = known.get(graphQLType.getName());
        if (resolvedType.equals(knownType)) {
            return ValidationResult.valid();
        }
        return ValidationResult.invalid(String.format("Potential type name collision detected: %s bound to multiple types: %s and %s." +
                        " Assign unique names using the appropriate annotations or override the %s." +
                        " For details and solutions see %s", graphQLType.getName(), knownType, resolvedType,
                TypeInfoGenerator.class.getSimpleName(), Urls.Errors.NON_UNIQUE_TYPE_NAME));
    }

    private AnnotatedType resolveType(GraphQLType graphQLType, Supplier<AnnotatedType> javaType) {
        AnnotatedType resolvedType;
        if (graphQLType instanceof MappedGraphQLType) {
            resolvedType = ((MappedGraphQLType) graphQLType).getJavaType();
        } else {
            resolvedType = javaType.get();
        }
        return ClassUtils.normalize(resolvedType);
    }

    static class ValidationResult {

        private final String message;

        private ValidationResult(String message) {
            this.message = message;
        }

        static ValidationResult valid() {
            return new ValidationResult(null);
        }

        static ValidationResult invalid(String message) {
            return new ValidationResult(message);
        }

        boolean isValid() {
            return message == null;
        }

        String getMessage() {
            return message;
        }
    }
}
