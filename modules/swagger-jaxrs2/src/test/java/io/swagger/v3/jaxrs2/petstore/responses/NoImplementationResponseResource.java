package io.swagger.v3.jaxrs2.petstore.responses;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.links.LinkParameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * Resource with a Response at Method Level
 * Created by rafaellopez on 11/27/17.
 */
public class NoImplementationResponseResource {
    @Path("/users")
    @Operation(operationId = "getUser",
            responses = {
                    @ApiResponse(description = "test description", responseCode = "400",
                            links = {
                                    @Link(
                                            name = "user",
                                            operationId = "getUser",
                                            operationRef = "#/components/links/MyLink",
                                            parameters = @LinkParameter(
                                                    name = "userId",
                                                    expression = "$request.query.userId"))
                            })}
    )
    @GET
    public NoImplementationResponseResource.User getUser(@QueryParam("userId") String userId) {
        return null;
    }

    static class User {
        private String id;
        private String username;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
