package io.leangen.graphql.metadata.strategy.query;

import graphql.execution.batched.Batched;
import io.leangen.graphql.annotations.GraphQLComplexity;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLSubscription;
import io.leangen.graphql.metadata.Resolver;
import io.leangen.graphql.metadata.execution.FieldAccessor;
import io.leangen.graphql.metadata.execution.MethodInvoker;
import io.leangen.graphql.metadata.execution.SingletonMethodInvoker;
import io.leangen.graphql.metadata.strategy.type.DefaultTypeTransformer;
import io.leangen.graphql.metadata.strategy.type.TypeTransformer;
import io.leangen.graphql.util.ClassUtils;
import io.leangen.graphql.util.Utils;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A resolver builder that exposes only the methods explicitly annotated with {@link GraphQLQuery}
 */
public class AnnotatedResolverBuilder extends FilteredResolverBuilder {

    public AnnotatedResolverBuilder() {
        this(new DefaultTypeTransformer(false, false));
    }
    
    public AnnotatedResolverBuilder(TypeTransformer transformer) {
        this.transformer = Objects.requireNonNull(transformer);
        this.operationNameGenerator = new DelegatingOperationNameGenerator(new AnnotatedOperationNameGenerator(), new MethodOperationNameGenerator());
        this.argumentBuilder = new AnnotatedArgumentBuilder(transformer);
        withDefaultFilters();
    }

    @Override
    public Collection<Resolver> buildQueryResolvers(Object querySourceBean, AnnotatedType beanType) {
        return buildQueryResolvers(querySourceBean, beanType, getFilters());
    }

    @Override
    public Collection<Resolver> buildMutationResolvers(Object querySourceBean, AnnotatedType beanType) {
        return buildMutationResolvers(querySourceBean, beanType, getFilters());
    }

    private Collection<Resolver> buildQueryResolvers(Object querySourceBean, AnnotatedType beanType, List<Predicate<Member>> filters) {
        Stream<Resolver> methodInvokers = ClassUtils.getAnnotatedMethods(ClassUtils.getRawType(beanType.getType()), GraphQLQuery.class).stream()
                .filter(filters.stream().reduce(Predicate::and).orElse(ACCEPT_ALL))
                .map(method -> new Resolver(
                        operationNameGenerator.generateQueryName(method, beanType, querySourceBean),
                        method.getAnnotation(GraphQLQuery.class).description(),
                        Utils.decodeNullable(method.getAnnotation(GraphQLQuery.class).deprecationReason()),
                        method.isAnnotationPresent(Batched.class),
                        querySourceBean == null ? new MethodInvoker(method, beanType) : new SingletonMethodInvoker(querySourceBean, method, beanType),
                        getReturnType(method, beanType),
                        argumentBuilder.buildResolverArguments(method, beanType),
                        method.isAnnotationPresent(GraphQLComplexity.class) ? method.getAnnotation(GraphQLComplexity.class).value() : null
                ));
        Stream<Resolver> fieldAccessors = ClassUtils.getAnnotatedFields(ClassUtils.getRawType(beanType.getType()), GraphQLQuery.class).stream()
                .filter(filters.stream().reduce(Predicate::and).orElse(ACCEPT_ALL))
                .map(field -> new Resolver(
                        operationNameGenerator.generateQueryName(field, beanType, querySourceBean),
                        field.getAnnotation(GraphQLQuery.class).description(),
                        Utils.decodeNullable(field.getAnnotation(GraphQLQuery.class).deprecationReason()),
                        false,
                        new FieldAccessor(field, beanType),
                        getFieldType(field, beanType),
                        Collections.emptyList(),
                        field.isAnnotationPresent(GraphQLComplexity.class) ? field.getAnnotation(GraphQLComplexity.class).value() : null
                ));
        return Stream.concat(methodInvokers, fieldAccessors).collect(Collectors.toSet());
    }

    private Collection<Resolver> buildMutationResolvers(Object querySourceBean, AnnotatedType beanType, List<Predicate<Member>> filters) {
        return ClassUtils.getAnnotatedMethods(ClassUtils.getRawType(beanType.getType()), GraphQLMutation.class).stream()
                .filter(filters.stream().reduce(Predicate::and).orElse(ACCEPT_ALL))
                .map(method -> new Resolver(
                        operationNameGenerator.generateMutationName(method, beanType, querySourceBean),
                        method.getAnnotation(GraphQLMutation.class).description(),
                        Utils.decodeNullable(method.getAnnotation(GraphQLMutation.class).deprecationReason()),
                        false,
                        querySourceBean == null ? new MethodInvoker(method, beanType) : new SingletonMethodInvoker(querySourceBean, method, beanType),
                        getReturnType(method, beanType),
                        argumentBuilder.buildResolverArguments(method, beanType),
                        method.isAnnotationPresent(GraphQLComplexity.class) ? method.getAnnotation(GraphQLComplexity.class).value() : null
                )).collect(Collectors.toSet());
    }

    @Override
    public Collection<Resolver> buildSubscriptionResolvers(Object querySourceBean, AnnotatedType beanType) {
        return ClassUtils.getAnnotatedMethods(ClassUtils.getRawType(beanType.getType()), GraphQLSubscription.class).stream()
                .filter(filters.stream().reduce(Predicate::and).orElse(ACCEPT_ALL))
                .map(method -> new Resolver(
                        operationNameGenerator.generateSubscriptionName(method, beanType, querySourceBean),
                        method.getAnnotation(GraphQLSubscription.class).description(),
                        Utils.decodeNullable(method.getAnnotation(GraphQLSubscription.class).deprecationReason()),
                        false,
                        querySourceBean == null ? new MethodInvoker(method, beanType) : new SingletonMethodInvoker(querySourceBean, method, beanType),
                        getReturnType(method, beanType),
                        argumentBuilder.buildResolverArguments(method, beanType),
                        method.isAnnotationPresent(GraphQLComplexity.class) ? method.getAnnotation(GraphQLComplexity.class).value() : null
                )).collect(Collectors.toSet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationNameGenerator.getClass(), argumentBuilder.getClass(), transformer.getClass());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AnnotatedResolverBuilder)) return false;
        AnnotatedResolverBuilder that = (AnnotatedResolverBuilder) other;
        return this.operationNameGenerator.getClass().equals(that.operationNameGenerator.getClass())
                && this.argumentBuilder.getClass().equals(that.argumentBuilder.getClass())
                && this.transformer.getClass().equals(that.transformer.getClass());
    }
}
