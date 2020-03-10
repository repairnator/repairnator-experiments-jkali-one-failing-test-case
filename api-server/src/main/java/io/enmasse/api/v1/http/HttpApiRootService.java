/*
 * Copyright 2018, EnMasse authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.enmasse.api.v1.http;

import io.enmasse.api.auth.RbacSecurityContext;
import io.enmasse.api.common.Exceptions;
import io.enmasse.api.v1.types.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;

@Path("/apis")
public class HttpApiRootService {
    private static final APIGroup apiGroup =
            new APIGroup("enmasse.io", Arrays.asList(
                    new APIGroupVersion("enmasse.io/v1alpha1", "v1alpha1")),
                    new APIGroupVersion("enmasse.io/v1alpha1", "v1alpha1"),
                    null);

    private static final APIGroupList apiGroupList = new APIGroupList(Arrays.asList(apiGroup));

    private static void verifyAuthorized(SecurityContext securityContext, String method, String path) {
        if (!securityContext.isUserInRole(RbacSecurityContext.rbacToRole(path, method))) {
            throw Exceptions.notAuthorizedException();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public APIGroupList getApiGroupList(@Context SecurityContext securityContext, @Context UriInfo uriInfo) {
        verifyAuthorized(securityContext, "get", uriInfo.getPath());
        return apiGroupList;
    }

    @GET
    @Path("enmasse.io")
    @Produces({MediaType.APPLICATION_JSON})
    public APIGroup getApiGroup(@Context SecurityContext securityContext, @Context UriInfo uriInfo) {
        verifyAuthorized(securityContext, "get", uriInfo.getPath());
        return apiGroup;
    }


    private static final APIResourceList apiResourceList = new APIResourceList("enmasse.io/v1alpha1",
        Arrays.asList(
                new APIResource("addressspaces", "", true, "AddressSpace",
                    Arrays.asList("create", "delete", "get", "list")),
                new APIResource("addresses", "", true, "Address",
                                Arrays.asList("create", "delete", "get", "list"))));

    @GET
    @Path("enmasse.io/v1alpha1")
    @Produces({MediaType.APPLICATION_JSON})
    public APIResourceList getApiGroupV1(@Context SecurityContext securityContext, @Context UriInfo uriInfo) {
        verifyAuthorized(securityContext, "get", uriInfo.getPath());
        return apiResourceList;
    }
}
