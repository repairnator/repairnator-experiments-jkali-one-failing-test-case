package io.leangen.graphql.generator.mapping.common;

import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.GraphQLUnionType;
import io.leangen.graphql.generator.BuildContext;
import io.leangen.graphql.generator.OperationMapper;
import io.leangen.graphql.generator.mapping.TypeMapper;
import io.leangen.graphql.generator.types.MappedGraphQLUnionType;
import io.leangen.graphql.metadata.exceptions.TypeMappingException;

import java.lang.reflect.AnnotatedType;
import java.util.List;

import static graphql.schema.GraphQLUnionType.newUnionType;

/**
 * @author Bojan Tomic (kaqqao)
 */
public abstract class UnionMapper implements TypeMapper {

    @SuppressWarnings("WeakerAccess")
    protected GraphQLOutputType toGraphQLUnion(String name, String description, AnnotatedType javaType, List<AnnotatedType> possibleJavaTypes,
                                               OperationMapper operationMapper, BuildContext buildContext) {

        if (buildContext.typeCache.contains(name)) {
            return new GraphQLTypeReference(name);
        }
        buildContext.typeCache.register(name);
        GraphQLUnionType.Builder builder = newUnionType()
                .name(name)
                .description(description)
                .typeResolver(buildContext.typeResolver);

        possibleJavaTypes.stream()
                .map(pos -> operationMapper.toGraphQLType(pos, buildContext))
                .forEach(type -> {
                    if (type instanceof GraphQLObjectType) {
                        builder.possibleType((GraphQLObjectType) type);
                    } else if (type instanceof GraphQLTypeReference) {
                        builder.possibleType((GraphQLTypeReference) type);
                    } else {
                        throw new TypeMappingException(type.getClass().getSimpleName() +
                                " is not a valid GraphQL union member. Only object types can be unionized.");
                    }
                });

        GraphQLUnionType union = new MappedGraphQLUnionType(builder.build(), javaType);
        for (int i = 0; i < possibleJavaTypes.size(); i++) {
            buildContext.typeRepository.registerCovariantType(union.getName(), possibleJavaTypes.get(i), union.getTypes().get(i));
        }
        return union;
    }

    @Override
    public GraphQLInputType toGraphQLInputType(AnnotatedType javaType, OperationMapper operationMapper, BuildContext buildContext) {
        throw new UnsupportedOperationException("GraphQL union type can not be used as an input type");
    }
}
