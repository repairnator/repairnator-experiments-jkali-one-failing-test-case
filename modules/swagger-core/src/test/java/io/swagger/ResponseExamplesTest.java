package io.swagger;

import io.swagger.matchers.SerializationMatchers;
import io.swagger.oas.models.examples.Example;
import io.swagger.oas.models.media.Content;
import io.swagger.oas.models.media.MediaType;
import io.swagger.oas.models.responses.ApiResponse;
import org.testng.annotations.Test;

import java.io.IOException;

public class ResponseExamplesTest {
    @Test(description = "it should create a response")
    public void createResponse() throws IOException {
        final ApiResponse response = new ApiResponse()
                .content(new Content()
                .addMediaType("application/json", new MediaType()
                .addExamples("test", new Example().value("{\"name\":\"Fred\",\"id\":123456\"}"))));

        final String json = "{\n" +
                "  \"content\" : {\n" +
                "    \"application/json\" : {\n" +
                "      \"examples\" : {\n" +
                "        \"test\" : {\n" +
                "          \"value\" : \"{\\\"name\\\":\\\"Fred\\\",\\\"id\\\":123456\\\"}\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        SerializationMatchers.assertEqualsToJson(response, json);
    }
}
