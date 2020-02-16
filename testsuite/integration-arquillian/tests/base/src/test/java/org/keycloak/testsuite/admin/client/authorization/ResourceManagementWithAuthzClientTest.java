/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.testsuite.admin.client.authorization;

import java.io.IOException;
import java.util.stream.Collectors;

import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.idm.authorization.ResourceOwnerRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.keycloak.util.JsonSerialization;

/**
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class ResourceManagementWithAuthzClientTest extends ResourceManagementTest {

    private AuthzClient authzClient;

    @Override
    protected ResourceRepresentation doCreateResource(ResourceRepresentation newResource) {
        org.keycloak.authorization.client.representation.ResourceRepresentation resource = toResourceRepresentation(newResource);

        AuthzClient authzClient = getAuthzClient();
        org.keycloak.authorization.client.representation.ResourceRepresentation response = authzClient.protection().resource().create(resource);

        return toResourceRepresentation(authzClient, response.getId());
    }

    @Override
    protected ResourceRepresentation doUpdateResource(ResourceRepresentation resource) {
        AuthzClient authzClient = getAuthzClient();

        authzClient.protection().resource().update(toResourceRepresentation(resource));

        return toResourceRepresentation(authzClient, resource.getId());
    }

    @Override
    protected void doRemoveResource(ResourceRepresentation resource) {
        getAuthzClient().protection().resource().delete(resource.getId());
    }

    private ResourceRepresentation toResourceRepresentation(AuthzClient authzClient, String id) {
        org.keycloak.authorization.client.representation.ResourceRepresentation created = authzClient.protection().resource().findById(id);
        ResourceRepresentation resourceRepresentation = new ResourceRepresentation();

        resourceRepresentation.setId(created.getId());
        resourceRepresentation.setName(created.getName());
        resourceRepresentation.setIconUri(created.getIconUri());
        resourceRepresentation.setUri(created.getUri());
        resourceRepresentation.setType(created.getType());
        ResourceOwnerRepresentation owner = new ResourceOwnerRepresentation();

        owner.setId(created.getOwner());

        resourceRepresentation.setOwner(owner);
        resourceRepresentation.setScopes(created.getScopes().stream().map(scopeRepresentation -> {
            ScopeRepresentation scope = new ScopeRepresentation();

            scope.setId(scopeRepresentation.getId());
            scope.setName(scopeRepresentation.getName());
            scope.setIconUri(scopeRepresentation.getIconUri());

            return scope;
        }).collect(Collectors.toSet()));

        resourceRepresentation.setAttributes(created.getAttributes());

        return resourceRepresentation;
    }

    private org.keycloak.authorization.client.representation.ResourceRepresentation toResourceRepresentation(ResourceRepresentation newResource) {
        org.keycloak.authorization.client.representation.ResourceRepresentation resource = new org.keycloak.authorization.client.representation.ResourceRepresentation();

        resource.setId(newResource.getId());
        resource.setName(newResource.getName());
        resource.setIconUri(newResource.getIconUri());
        resource.setUri(newResource.getUri());
        resource.setType(newResource.getType());

        if (newResource.getOwner() != null) {
            resource.setOwner(newResource.getOwner().getId());
        }

        resource.setScopes(newResource.getScopes().stream().map(scopeRepresentation -> {
            org.keycloak.authorization.client.representation.ScopeRepresentation scope = new org.keycloak.authorization.client.representation.ScopeRepresentation();

            scope.setName(scopeRepresentation.getName());
            scope.setIconUri(scopeRepresentation.getIconUri());

            return scope;
        }).collect(Collectors.toSet()));

        resource.setAttributes(newResource.getAttributes());

        return resource;
    }

    private AuthzClient getAuthzClient() {
        if (authzClient == null) {
            try {
                authzClient = AuthzClient.create(JsonSerialization.readValue(getClass().getResourceAsStream("/authorization-test/default-keycloak.json"), Configuration.class));
            } catch (IOException cause) {
                throw new RuntimeException("Failed to create authz client", cause);
            }
        }

        return authzClient;
    }
}