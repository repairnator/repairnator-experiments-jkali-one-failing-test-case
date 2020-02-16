/*
 *  Copyright 2016 Red Hat, Inc. and/or its affiliates
 *  and other contributors as indicated by the @author tags.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.keycloak.representations.adapters.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class PolicyEnforcerConfig {

    @JsonProperty("create-resources")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean createResources = Boolean.FALSE;

    @JsonProperty("enforcement-mode")
    private EnforcementMode enforcementMode = EnforcementMode.ENFORCING;

    @JsonProperty("paths")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<PathConfig> paths = new ArrayList<>();

    @JsonProperty("on-deny-redirect-to")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String onDenyRedirectTo;

    @JsonProperty("user-managed-access")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserManagedAccessConfig userManagedAccess;

    public Boolean isCreateResources() {
        return this.createResources;
    }

    public List<PathConfig> getPaths() {
        return this.paths;
    }

    public EnforcementMode getEnforcementMode() {
        return this.enforcementMode;
    }

    public void setEnforcementMode(EnforcementMode enforcementMode) {
        this.enforcementMode = enforcementMode;
    }

    public UserManagedAccessConfig getUserManagedAccess() {
        return this.userManagedAccess;
    }

    public void setCreateResources(Boolean createResources) {
        this.createResources = createResources;
    }

    public void setPaths(List<PathConfig> paths) {
        this.paths = paths;
    }

    public String getOnDenyRedirectTo() {
        return onDenyRedirectTo;
    }

    public void setUserManagedAccess(UserManagedAccessConfig userManagedAccess) {
        this.userManagedAccess = userManagedAccess;
    }

    public void setOnDenyRedirectTo(String onDenyRedirectTo) {
        this.onDenyRedirectTo = onDenyRedirectTo;
    }

    public static class PathConfig {

        private String name;
        private String type;
        private String path;
        private List<MethodConfig> methods = new ArrayList<>();
        private List<String> scopes = new ArrayList<>();
        private String id;

        @JsonProperty("enforcement-mode")
        private EnforcementMode enforcementMode = EnforcementMode.ENFORCING;

        @JsonIgnore
        private PathConfig parentConfig;

        public String getPath() {
            return this.path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public List<String> getScopes() {
            return this.scopes;
        }

        public void setScopes(List<String> scopes) {
            this.scopes = scopes;
        }

        public List<MethodConfig> getMethods() {
            return methods;
        }

        public void setMethods(List<MethodConfig> methods) {
            this.methods = methods;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public EnforcementMode getEnforcementMode() {
            return enforcementMode;
        }

        public void setEnforcementMode(EnforcementMode enforcementMode) {
            this.enforcementMode = enforcementMode;
        }

        @Override
        public String toString() {
            return "PathConfig{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", path='" + path + '\'' +
                    ", scopes=" + scopes +
                    ", id='" + id + '\'' +
                    ", enforcerMode='" + enforcementMode + '\'' +
                    '}';
        }

        @JsonIgnore
        public boolean hasPattern() {
            return getPath().indexOf("{") != -1;
        }

        @JsonIgnore
        public boolean isInstance() {
            return this.parentConfig != null;
        }

        public void setParentConfig(PathConfig parentConfig) {
            this.parentConfig = parentConfig;
        }

        public PathConfig getParentConfig() {
            return parentConfig;
        }
    }

    public static class MethodConfig {

        private String method;
        private List<String> scopes = new ArrayList<>();

        @JsonProperty("scopes-enforcement-mode")
        private ScopeEnforcementMode scopesEnforcementMode = ScopeEnforcementMode.ALL;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public List<String> getScopes() {
            return scopes;
        }

        public void setScopes(List<String> scopes) {
            this.scopes = scopes;
        }

        public void setScopesEnforcementMode(ScopeEnforcementMode scopesEnforcementMode) {
            this.scopesEnforcementMode = scopesEnforcementMode;
        }

        public ScopeEnforcementMode getScopesEnforcementMode() {
            return scopesEnforcementMode;
        }
    }

    public enum EnforcementMode {
        PERMISSIVE,
        ENFORCING,
        DISABLED
    }

    public enum ScopeEnforcementMode {
        ALL,
        ANY
    }

    public static class UserManagedAccessConfig {

    }
}
