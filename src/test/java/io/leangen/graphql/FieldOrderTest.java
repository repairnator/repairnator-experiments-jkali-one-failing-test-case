package io.leangen.graphql;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.introspection.IntrospectionQuery;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.types.GraphQLInterface;
import io.leangen.graphql.annotations.types.GraphQLType;

public class FieldOrderTest {

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        ExecutionResult result = executeQuery(IntrospectionQuery.INTROSPECTION_QUERY);

        List<String> characterFields = getTypeFieldsFromIntrospectionResult(result, "Character");
        Assert.assertEquals(Arrays.asList("id", "name", "friends", "appearsIn", "_type_"), characterFields);

        List<String> humanFields = getTypeFieldsFromIntrospectionResult(result, "Human");
        Assert.assertEquals(Arrays.asList("id", "name", "friends", "appearsIn", "starships", "totalCredits", "_type_"), humanFields);

        List<String> droidFields = getTypeFieldsFromIntrospectionResult(result, "Droid");
        Assert.assertEquals(Arrays.asList("id", "_type_", "appearsIn", "friends", "name", "primaryFunction"), droidFields);
    }

    public static class Query {
        @GraphQLQuery
        public Character hero() { return null; }
    }

    @GraphQLInterface(name = "Character", implementationAutoDiscovery = true, fieldOrder = {"id", "name", "friends", "appearsIn"})
    public interface Character {
        String getId();
        String getName();
        String getFriends();
        String getAppearsIn();
    }

    @GraphQLType(name = "Human", fieldOrder = {"id", "name", "friends", "appearsIn", "starships", "totalCredits"})
    public static class Human implements Character {
        public String getId() { return null; }
        public String getName() { return null; }
        public String getFriends() { return null; }
        public String getAppearsIn() { return null; }
        public String getStarships() { return null; }
        public String getTotalCredits() { return null; }
    }

    @GraphQLType(name = "Droid", fieldOrder = {"id" /*rest alphabetically*/})
    public static class Droid implements Character {
        public String getId() { return null; }
        public String getName() { return null; }
        public String getFriends() { return null; }
        public String getAppearsIn() { return null; }
        public String getPrimaryFunction() { return null; }
    }

    private static ExecutionResult executeQuery(String query) {
        GraphQLSchema schema = new GraphQLSchemaGenerator()
                .withBasePackages(FieldOrderTest.class.getPackage().getName())
                .withOperationsFromSingletons(new Query())
                .generate();
        GraphQL graphQL = GraphQL.newGraphQL(schema).build();
        return graphQL.execute(query);
    }

    @SuppressWarnings("unchecked")
    private static List<String> getTypeFieldsFromIntrospectionResult(ExecutionResult result, String typeName) {
        Map<String, Object> data = result.getData();
        Map<String, Object> __schema = (Map<String, Object>) data.get("__schema");
        List<Map<String, Object>> types = (List<Map<String, Object>>) __schema.get("types");
        Map<String, Object> type = types.stream()
                .filter(t -> Objects.equals(t.get("name"), typeName))
                .findFirst()
                .get();
        List<Map<String, Object>> fields = (List<Map<String, Object>>)type.get("fields");
        return fields.stream()
                .map(field -> (String) field.get("name"))
                .collect(Collectors.toList());
    }

}
