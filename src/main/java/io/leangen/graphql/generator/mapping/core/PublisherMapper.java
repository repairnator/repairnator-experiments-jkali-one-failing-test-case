package io.leangen.graphql.generator.mapping.core;

import graphql.schema.GraphQLInputType;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.graphql.generator.BuildContext;
import io.leangen.graphql.generator.OperationMapper;
import io.leangen.graphql.generator.mapping.common.AbstractTypeSubstitutingMapper;
import io.leangen.graphql.util.ClassUtils;
import org.reactivestreams.Publisher;

import java.lang.reflect.AnnotatedType;

public class PublisherMapper extends AbstractTypeSubstitutingMapper {

    @Override
    public GraphQLInputType toGraphQLInputType(AnnotatedType javaType, OperationMapper operationMapper, BuildContext buildContext) {
        throw new UnsupportedOperationException(Publisher.class.getSimpleName() + " can not be used as an input type");
    }

    @Override
    public AnnotatedType getSubstituteType(AnnotatedType original) {
        AnnotatedType innerType = GenericTypeReflector.getTypeParameter(original, Publisher.class.getTypeParameters()[0]);
        return ClassUtils.addAnnotations(innerType, original.getAnnotations());
    }

    @Override
    public boolean supports(AnnotatedType type) {
        return GenericTypeReflector.isSuperType(Publisher.class, type.getType());
    }
}