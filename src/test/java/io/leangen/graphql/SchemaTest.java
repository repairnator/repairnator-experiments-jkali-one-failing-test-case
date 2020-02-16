package io.leangen.graphql;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.geantyref.TypeToken;
import io.leangen.graphql.domain.Education;
import io.leangen.graphql.generator.mapping.common.MapToListTypeAdapter;
import io.leangen.graphql.generator.mapping.strategy.ObjectScalarStrategy;
import io.leangen.graphql.metadata.strategy.value.ValueMapperFactory;
import io.leangen.graphql.metadata.strategy.value.gson.GsonValueMapperFactory;
import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory;
import io.leangen.graphql.services.UserService;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class SchemaTest {

    private static final String simpleQuery = "{users(regDate: 1465667452785) {" +
            "id, name, title, zmajs, addresses {" +
            "types" +
            "}}}";

    private static final String simpleQueryWithNullInput = "{usersByDate {" +
            "id, name, title, zmajs, addresses {" +
            "types" +
            "}}}";

    private static final String complexInputQuery = "{users (education: {" +
            "schoolName: \"tshc\"," +
            "startYear: 1999," +
            "endYear: 2003}) {" +
            "name" +
            "}}";

    private static final String complexGenericInputQuery = "{usersArr (educations: [" +
            "{schoolName: \"tshc\"," +
            "startYear: 1999," +
            "endYear: 2003," +
            "tier: TOP}," +

            "{schoolName: \"other\"," +
            "startYear: 1999," +
            "endYear: 2003," +
            "tier: BOTTOM}]) {" +
            "name" +
            "}}";

    private static final String mutation = "mutation M {" +
            "  updateUsername(username: \"OMG NEW USERNAME!!11\") {" +
            "    id" +
            "    name" +
            "    addresses {" +
            "      types" +
            "    }" +
            "  }" +
            "}";

    private static final String mapInputMutation = "mutation M {" +
            "upMe (updates: {" +
            "       key: \"name\"," +
            "       value: \"New Dyno\"}) {" +
            "   key" +
            "   value" +
            "}}";

    private static final String simpleFragmentQuery = "{" +
            "  users(regDate: 1465667452785) {" +
            "    ...userInfo" +
            "    uuid" +
            "  }" +
            "}" +
            "" +
            "fragment userInfo on User_String {" +
            "  name," +
            "  title," +
            "  regDate" +
            "}";

    @Parameterized.Parameter
    public ValueMapperFactory valueMapperFactory;

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Object[] data() {
        return new Object[] { new JacksonValueMapperFactory(), new GsonValueMapperFactory() };
    }

    @Test
    public void testSchema() {
        GraphQLSchema schema = new TestSchemaGenerator()
                .withValueMapperFactory(valueMapperFactory)
                .withTypeAdapters(new MapToListTypeAdapter<>(new ObjectScalarStrategy()))
                .withDefaults()
                .withOperationsFromSingleton(new UserService<Education>(), new TypeToken<UserService<Education>>(){}.getAnnotatedType())
                .generate();

        List<String> context = Arrays.asList("xxx", "zzz", "yyy");
        GraphQL exe = GraphQLRuntime.newGraphQL(schema).build();
        ExecutionResult result;

        result = execute(exe, simpleFragmentQuery, context);
        assertTrue(result.getErrors().isEmpty());
        result = execute(exe, complexGenericInputQuery, context);
        assertTrue(result.getErrors().isEmpty());
        result = execute(exe, simpleQuery, context);
        assertTrue(result.getErrors().isEmpty());
        result = execute(exe, simpleQueryWithNullInput, context);
        assertTrue(result.getErrors().isEmpty());
        result = execute(exe, mapInputMutation, context);
        assertTrue(result.getErrors().isEmpty());
    }

    private ExecutionResult execute(GraphQL graphQL, String operation, Object context) {
        return graphQL.execute(ExecutionInput.newExecutionInput().query(operation).context(context).build());
    }
}
