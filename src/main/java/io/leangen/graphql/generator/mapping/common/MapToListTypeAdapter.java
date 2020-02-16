package io.leangen.graphql.generator.mapping.common;

import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeFactory;
import io.leangen.graphql.execution.GlobalEnvironment;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.generator.BuildContext;
import io.leangen.graphql.generator.OperationMapper;
import io.leangen.graphql.generator.mapping.AbstractTypeAdapter;
import io.leangen.graphql.generator.mapping.strategy.ScalarMappingStrategy;
import io.leangen.graphql.metadata.strategy.value.ValueMapper;
import io.leangen.graphql.util.ClassUtils;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;

/**
 * As maps are dynamic structures with no equivalent in GraphQL, they require special treatment.
 * This adapter turns a map into a list of key-value pairs (instances of {@link MapToListTypeAdapter.MapEntry}).
 */
public class MapToListTypeAdapter<K,V> extends AbstractTypeAdapter<Map<K,V>, List<MapToListTypeAdapter.MapEntry<K,V>>> {

    private final ScalarMappingStrategy scalarStrategy;

    public MapToListTypeAdapter(ScalarMappingStrategy scalarStrategy) {
        this.scalarStrategy = Objects.requireNonNull(scalarStrategy);
    }

    @Override
    public List<MapToListTypeAdapter.MapEntry<K,V>> convertOutput(Map<K, V> original, AnnotatedType type, ResolutionEnvironment resolutionEnvironment) {
        return original.entrySet().stream()
                .map(entry -> new MapToListTypeAdapter.MapEntry<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<K,V> convertInput(List<MapEntry<K, V>> original, AnnotatedType type, GlobalEnvironment environment, ValueMapper valueMapper) {
        Map<K, V> initial = GenericTypeReflector.isSuperType(type.getType(), HashMap.class) ? new HashMap<>() : ClassUtils.instance(type);
        return original.stream().collect(toMap(MapEntry::getKey, MapEntry::getValue, initial));
    }

    @Override
    public AnnotatedType getSubstituteType(AnnotatedType original) {
        AnnotatedType keyType = getElementType(original, 0);
        AnnotatedType valueType = getElementType(original, 1);
        Type entryType = TypeFactory.parameterizedClass(MapToListTypeAdapter.MapEntry.class, keyType.getType(), valueType.getType());
        return GenericTypeReflector.annotate(TypeFactory.parameterizedClass(List.class, entryType), original.getAnnotations());
    }

    @Override
    public GraphQLOutputType toGraphQLType(AnnotatedType javaType, OperationMapper operationMapper, BuildContext buildContext) {
        return new GraphQLList(
                mapEntry(
                        operationMapper.toGraphQLType(getElementType(javaType, 0), buildContext),
                        operationMapper.toGraphQLType(getElementType(javaType, 1), buildContext), buildContext));
    }

    @Override
    public GraphQLInputType toGraphQLInputType(AnnotatedType javaType, OperationMapper operationMapper, BuildContext buildContext) {
        return new GraphQLList(
                mapEntry(
                        operationMapper.toGraphQLInputType(getElementType(javaType, 0), buildContext),
                        operationMapper.toGraphQLInputType(getElementType(javaType, 1), buildContext), buildContext));
    }

    @Override
    public boolean supports(AnnotatedType type) {
        return super.supports(type) && !scalarStrategy.supports(type);
    }

    private GraphQLOutputType mapEntry(GraphQLOutputType keyType, GraphQLOutputType valueType, BuildContext buildContext) {
        String typeName = "mapEntry_" + keyType.getName() + "_" + valueType.getName();
        if (buildContext.typeCache.contains(typeName)) {
            return new GraphQLTypeReference(typeName);
        }
        buildContext.typeCache.register(typeName);

        return newObject()
                .name(typeName)
                .description("Map entry")
                .field(newFieldDefinition()
                        .name("key")
                        .description("Map key")
                        .type(keyType)
                        .build())
                .field(newFieldDefinition()
                        .name("value")
                        .description("Map value")
                        .type(valueType)
                        .build())
                .build();
    }

    private GraphQLInputType mapEntry(GraphQLInputType keyType, GraphQLInputType valueType, BuildContext buildContext) {
        String typeName = "mapEntry_" + keyType.getName() + "_" + valueType.getName() + "_input";
        if (buildContext.typeCache.contains(typeName)) {
            return new GraphQLTypeReference(typeName);
        }
        buildContext.typeCache.register(typeName);

        return newInputObject()
                .name(typeName)
                .description("Map entry input")
                .field(newInputObjectField()
                        .name("key")
                        .description("Map key input")
                        .type(keyType)
                        .build())
                .field(newInputObjectField()
                        .name("value")
                        .description("Map value input")
                        .type(valueType)
                        .build())
                .build();
    }

    private AnnotatedType getElementType(AnnotatedType javaType, int index) {
        return GenericTypeReflector.getTypeParameter(javaType, Map.class.getTypeParameters()[index]);
    }

    private static <T, K, U> Collector<T, ?, Map<K,U>> toMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper,
            Map<K,U> initial) {
        return Collectors.toMap(keyMapper, valueMapper,
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                () -> initial);
    }

    @SuppressWarnings("WeakerAccess")
    public static class MapEntry<K, V> {
        private K key;
        private V value;

        public MapEntry() {
        }

        public MapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
