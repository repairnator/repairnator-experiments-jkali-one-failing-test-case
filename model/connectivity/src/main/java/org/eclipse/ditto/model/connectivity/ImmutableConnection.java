/*
 * Copyright (c) 2017 Bosch Software Innovations GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/index.php
 *
 * Contributors:
 *    Bosch Software Innovations GmbH - initial contribution
 */
package org.eclipse.ditto.model.connectivity;

import static org.eclipse.ditto.model.base.common.ConditionChecker.checkArgument;
import static org.eclipse.ditto.model.base.common.ConditionChecker.checkNotNull;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.eclipse.ditto.json.JsonArray;
import org.eclipse.ditto.json.JsonCollectors;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonMissingFieldException;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonParseException;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.model.base.auth.AuthorizationContext;
import org.eclipse.ditto.model.base.auth.AuthorizationModelFactory;
import org.eclipse.ditto.model.base.auth.AuthorizationSubject;
import org.eclipse.ditto.model.base.json.JsonSchemaVersion;

/**
 * Immutable implementation of {@link Connection}.
 */
@Immutable
final class ImmutableConnection implements Connection {

    private final String id;
    @Nullable private final String name;
    private final ConnectionType connectionType;
    private final ConnectionStatus connectionStatus;
    private final AuthorizationContext authorizationContext;
    private final ConnectionUri uri;

    private final Set<Source> sources;
    private final Set<Target> targets;
    private final int clientCount;
    private final boolean failOverEnabled;
    private final boolean validateCertificate;
    private final int processorPoolSize;
    private final Map<String, String> specificConfig;
    @Nullable private final MappingContext mappingContext;
    private final Set<String> tags;

    private ImmutableConnection(final Builder builder) {
        id = builder.id;
        name = builder.name;
        connectionType = builder.connectionType;
        connectionStatus = builder.connectionStatus;
        uri = ConnectionUri.of(builder.uri);
        authorizationContext = builder.authorizationContext;
        sources = Collections.unmodifiableSet(new HashSet<>(builder.sources));
        targets = Collections.unmodifiableSet(new HashSet<>(builder.targets));
        clientCount = builder.clientCount;
        failOverEnabled = builder.failOverEnabled;
        validateCertificate = builder.validateCertificate;
        processorPoolSize = builder.processorPoolSize;
        specificConfig = Collections.unmodifiableMap(new HashMap<>(builder.specificConfig));
        mappingContext = builder.mappingContext;
        tags = Collections.unmodifiableSet(new HashSet<>(builder.tags));
    }

    /**
     * Returns a new {@code ConnectionBuilder} object.
     *
     * @param id the connection ID.
     * @param connectionType the connection type.
     * @param connectionStatus the connection status.
     * @param uri the URI.
     * @param authorizationContext the authorization context.
     * @return new instance of {@code ConnectionBuilder}.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static ConnectionBuilder getBuilder(final String id,
            final ConnectionType connectionType,
            final ConnectionStatus connectionStatus,
            final String uri,
            final AuthorizationContext authorizationContext) {

        return new Builder(connectionType)
                .id(id)
                .connectionStatus(connectionStatus)
                .uri(uri)
                .authorizationContext(authorizationContext);
    }

    /**
     * Returns a new {@code ConnectionBuilder} object.
     *
     * @param connection the connection to use for initializing the builder.
     * @return new instance of {@code ImmutableConnectionBuilder}.
     * @throws NullPointerException if {@code connection} is {@code null}.
     */
    public static ConnectionBuilder getBuilder(final Connection connection) {
        checkNotNull(connection, "Connection");

        return new Builder(connection.getConnectionType())
                .id(connection.getId())
                .connectionStatus(connection.getConnectionStatus())
                .uri(connection.getUri())
                .authorizationContext(connection.getAuthorizationContext())
                .failoverEnabled(connection.isFailoverEnabled())
                .validateCertificate(connection.isValidateCertificates())
                .processorPoolSize(connection.getProcessorPoolSize())
                .sources(connection.getSources())
                .targets(connection.getTargets())
                .clientCount(connection.getClientCount())
                .specificConfig(connection.getSpecificConfig())
                .mappingContext(connection.getMappingContext().orElse(null))
                .name(connection.getName().orElse(null))
                .tags(connection.getTags());
    }

    /**
     * Creates a new {@code Connection} object from the specified JSON object.
     *
     * @param jsonObject a JSON object which provides the data for the Connection to be created.
     * @return a new Connection which is initialised with the extracted data from {@code jsonObject}.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if {@code jsonObject} is not an appropriate JSON object.
     */
    public static Connection fromJson(final JsonObject jsonObject) {
        final ConnectionBuilder builder = new Builder(getConnectionTypeOrThrow(jsonObject))
                .id(jsonObject.getValueOrThrow(JsonFields.ID))
                .connectionStatus(getConnectionStatusOrThrow(jsonObject))
                .uri(jsonObject.getValueOrThrow(JsonFields.URI))
                .authorizationContext(getAuthorizationContext(jsonObject))
                .sources(getSources(jsonObject))
                .targets(getTargets(jsonObject))
                .name(jsonObject.getValue(JsonFields.NAME).orElse(null))
                .mappingContext(jsonObject.getValue(JsonFields.MAPPING_CONTEXT)
                        .map(ConnectivityModelFactory::mappingContextFromJson)
                        .orElse(null))
                .specificConfig(getSpecificConfiguration(jsonObject))
                .tags(getTags(jsonObject));

        jsonObject.getValue(JsonFields.CLIENT_COUNT).ifPresent(builder::clientCount);
        jsonObject.getValue(JsonFields.FAILOVER_ENABLED).ifPresent(builder::failoverEnabled);
        jsonObject.getValue(JsonFields.VALIDATE_CERTIFICATES).ifPresent(builder::validateCertificate);
        jsonObject.getValue(JsonFields.PROCESSOR_POOL_SIZE).ifPresent(builder::processorPoolSize);

        return builder.build();
    }

    private static ConnectionType getConnectionTypeOrThrow(final JsonObject jsonObject) {
        final String readConnectionType = jsonObject.getValueOrThrow(JsonFields.CONNECTION_TYPE);
        return ConnectionType.forName(readConnectionType)
                .orElseThrow(() -> JsonParseException.newBuilder()
                        .message(MessageFormat.format("Connection type <{0}> is invalid!", readConnectionType))
                        .build());
    }

    private static ConnectionStatus getConnectionStatusOrThrow(final JsonObject jsonObject) {
        final String readConnectionStatus = jsonObject.getValueOrThrow(JsonFields.CONNECTION_STATUS);
        return ConnectionStatus.forName(readConnectionStatus)
                .orElseThrow(() -> JsonParseException.newBuilder()
                        .message(MessageFormat.format("Connection status <{0}> is invalid!", readConnectionStatus))
                        .build());
    }

    private static AuthorizationContext getAuthorizationContext(final JsonObject jsonObject) {
        // as a fallback use the already persisted "authorizationSubject" field
        final JsonArray authSubjectsJsonArray = jsonObject.getValue(JsonFields.AUTHORIZATION_CONTEXT)
                .orElseGet(() -> jsonObject.getValue("authorizationSubject")
                        .filter(JsonValue::isString)
                        .map(JsonValue::asString)
                        .map(str -> JsonArray.newBuilder().add(str).build())
                        .orElseThrow(() -> new JsonMissingFieldException(JsonFields.AUTHORIZATION_CONTEXT)));
        final List<AuthorizationSubject> authorizationSubjects = authSubjectsJsonArray.stream()
                .filter(JsonValue::isString)
                .map(JsonValue::asString)
                .map(AuthorizationSubject::newInstance)
                .collect(Collectors.toList());

        return AuthorizationModelFactory.newAuthContext(authorizationSubjects);
    }

    private static Set<Source> getSources(final JsonObject jsonObject) {
        return jsonObject.getValue(JsonFields.SOURCES)
                .map(array -> array.stream()
                        .filter(JsonValue::isObject)
                        .map(JsonValue::asObject)
                        .map(ImmutableSource::fromJson)
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    private static Set<Target> getTargets(final JsonObject jsonObject) {
        return jsonObject.getValue(JsonFields.TARGETS)
                .map(array -> array.stream()
                        .filter(JsonValue::isObject)
                        .map(JsonValue::asObject)
                        .map(ImmutableTarget::fromJson)
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    private static Map<String, String> getSpecificConfiguration(final JsonObject jsonObject) {
        return jsonObject.getValue(JsonFields.SPECIFIC_CONFIG)
                .filter(JsonValue::isObject)
                .map(JsonValue::asObject)
                .map(JsonObject::stream)
                .map(jsonFields -> jsonFields.collect(Collectors.toMap(JsonField::getKeyName,
                        f -> f.getValue().isString() ? f.getValue().asString() : f.getValue().toString())))
                .orElse(Collections.emptyMap());
    }

    private static Set<String> getTags(final JsonObject jsonObject) {
        return jsonObject.getValue(JsonFields.TAGS)
                .map(array -> array.stream()
                        .filter(JsonValue::isString)
                        .map(JsonValue::asString)
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public ConnectionType getConnectionType() {
        return connectionType;
    }

    @Override
    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    @Override
    public AuthorizationContext getAuthorizationContext() {
        return authorizationContext;
    }

    @Override
    public Set<Source> getSources() {
        return sources;
    }

    @Override
    public Set<Target> getTargets() {
        return targets;
    }

    @Override
    public int getClientCount() {
        return clientCount;
    }

    @Override
    public boolean isFailoverEnabled() {
        return failOverEnabled;
    }

    @Override
    public String getUri() {
        return uri.toString();
    }

    @Override
    public String getProtocol() {
        return uri.getProtocol();
    }

    @Override
    public Optional<String> getUsername() {
        return uri.getUserName();
    }

    @Override
    public Optional<String> getPassword() {
        return uri.getPassword();
    }

    @Override
    public String getHostname() {
        return uri.getHostname();
    }

    @Override
    public int getPort() {
        return uri.getPort();
    }

    @Override
    public String getPath() {
        return uri.getPath();
    }

    @Override
    public boolean isValidateCertificates() {
        return validateCertificate;
    }

    @Override
    public int getProcessorPoolSize() {
        return processorPoolSize;
    }

    @Override
    public Map<String, String> getSpecificConfig() {
        return specificConfig;
    }

    @Override
    public Optional<MappingContext> getMappingContext() {
        return Optional.ofNullable(mappingContext);
    }

    @Override
    public Set<String> getTags() {
        return tags;
    }

    @Override
    public JsonObject toJson(final JsonSchemaVersion schemaVersion, final Predicate<JsonField> thePredicate) {
        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
        final JsonObjectBuilder jsonObjectBuilder = JsonFactory.newObjectBuilder();

        jsonObjectBuilder.set(JsonFields.SCHEMA_VERSION, schemaVersion.toInt(), predicate);
        jsonObjectBuilder.set(JsonFields.ID, id, predicate);
        jsonObjectBuilder.set(JsonFields.NAME, name, predicate);
        jsonObjectBuilder.set(JsonFields.CONNECTION_TYPE, connectionType.getName(), predicate);
        jsonObjectBuilder.set(JsonFields.CONNECTION_STATUS, connectionStatus.getName(), predicate);
        jsonObjectBuilder.set(JsonFields.URI, uri.toString(), predicate);
        jsonObjectBuilder.set(JsonFields.AUTHORIZATION_CONTEXT, authorizationContext.stream()
                .map(AuthorizationSubject::getId)
                .map(JsonFactory::newValue)
                .collect(JsonCollectors.valuesToArray()), predicate);
        jsonObjectBuilder.set(JsonFields.SOURCES, sources.stream()
                .map(source -> source.toJson(schemaVersion, thePredicate))
                .collect(JsonCollectors.valuesToArray()), predicate.and(Objects::nonNull));
        jsonObjectBuilder.set(JsonFields.TARGETS, targets.stream()
                .map(source -> source.toJson(schemaVersion, thePredicate))
                .collect(JsonCollectors.valuesToArray()), predicate.and(Objects::nonNull));
        jsonObjectBuilder.set(JsonFields.CLIENT_COUNT, clientCount, predicate);
        jsonObjectBuilder.set(JsonFields.FAILOVER_ENABLED, failOverEnabled, predicate);
        jsonObjectBuilder.set(JsonFields.VALIDATE_CERTIFICATES, validateCertificate, predicate);
        jsonObjectBuilder.set(JsonFields.PROCESSOR_POOL_SIZE, processorPoolSize, predicate);
        if (!specificConfig.isEmpty()) {
            jsonObjectBuilder.set(JsonFields.SPECIFIC_CONFIG, specificConfig.entrySet()
                    .stream()
                    .map(entry -> JsonField.newInstance(entry.getKey(), JsonValue.of(entry.getValue())))
                    .collect(JsonCollectors.fieldsToObject()), predicate);
        }
        if (mappingContext != null) {
            jsonObjectBuilder.set(JsonFields.MAPPING_CONTEXT, mappingContext.toJson(schemaVersion, thePredicate),
                    predicate);
        }
        jsonObjectBuilder.set(JsonFields.TAGS, tags.stream()
                .map(JsonFactory::newValue)
                .collect(JsonCollectors.valuesToArray()), predicate);
        return jsonObjectBuilder.build();
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ImmutableConnection that = (ImmutableConnection) o;
        return failOverEnabled == that.failOverEnabled &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(connectionType, that.connectionType) &&
                Objects.equals(connectionStatus, that.connectionStatus) &&
                Objects.equals(authorizationContext, that.authorizationContext) &&
                Objects.equals(sources, that.sources) &&
                Objects.equals(targets, that.targets) &&
                Objects.equals(clientCount, that.clientCount) &&
                Objects.equals(uri, that.uri) &&
                Objects.equals(processorPoolSize, that.processorPoolSize) &&
                Objects.equals(validateCertificate, that.validateCertificate) &&
                Objects.equals(specificConfig, that.specificConfig) &&
                Objects.equals(mappingContext, that.mappingContext) &&
                Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, connectionType, connectionStatus, authorizationContext, sources, targets,
                clientCount, failOverEnabled, uri, validateCertificate, processorPoolSize, specificConfig,
                mappingContext, tags);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "id=" + id +
                ", name=" + name +
                ", connectionType=" + connectionType +
                ", connectionStatus=" + connectionStatus +
                ", authorizationContext=" + authorizationContext +
                ", failoverEnabled=" + failOverEnabled +
                ", uri=" + uri +
                ", sources=" + sources +
                ", targets=" + targets +
                ", clientCount=" + clientCount +
                ", validateCertificate=" + validateCertificate +
                ", processorPoolSize=" + processorPoolSize +
                ", specificConfig=" + specificConfig +
                ", mappingContext=" + mappingContext +
                ", tags=" + tags +
                "]";
    }

    /**
     * Builder for {@code ImmutableConnection}.
     */
    @NotThreadSafe
    private static final class Builder implements ConnectionBuilder {

        private final ConnectionType connectionType;

        // required but changeable:
        private String id;
        private AuthorizationContext authorizationContext;
        private ConnectionStatus connectionStatus;
        private String uri;

        // optional:
        private Set<String> tags = new HashSet<>();
        private boolean failOverEnabled = true;
        private boolean validateCertificate = true;
        private final Set<Source> sources = new HashSet<>();
        private final Set<Target> targets = new HashSet<>();
        private int clientCount = 1;
        private int processorPoolSize = 5;
        private final Map<String, String> specificConfig = new HashMap<>();
        private @Nullable MappingContext mappingContext = null;
        private @Nullable String name = null;

        private Builder(final ConnectionType connectionType) {
            this.connectionType = checkNotNull(connectionType, "Connection Type");
        }

        @Override
        public ConnectionBuilder id(final String id) {
            this.id = checkNotNull(id, "ID");
            return this;
        }

        @Override
        public ConnectionBuilder name(@Nullable final String name) {
            this.name = name;
            return this;
        }

        @Override
        public ConnectionBuilder authorizationContext(final AuthorizationContext authorizationContext) {
            this.authorizationContext = checkNotNull(authorizationContext, "AuthorizationContext");
            return this;
        }

        @Override
        public ConnectionBuilder uri(final String uri) {
            this.uri = checkNotNull(uri, "URI");
            return this;
        }

        @Override
        public ConnectionBuilder connectionStatus(final ConnectionStatus connectionStatus) {
            this.connectionStatus = checkNotNull(connectionStatus, "ConnectionStatus");
            return this;
        }

        @Override
        public ConnectionBuilder failoverEnabled(final boolean failOverEnabled) {
            this.failOverEnabled = failOverEnabled;
            return this;
        }

        @Override
        public ConnectionBuilder validateCertificate(final boolean validateCertificate) {
            this.validateCertificate = validateCertificate;
            return this;
        }

        @Override
        public ConnectionBuilder processorPoolSize(final int processorPoolSize) {
            checkArgument(processorPoolSize, ps -> ps > 0, () -> "The consumer count must be positive!");
            this.processorPoolSize = processorPoolSize;
            return this;
        }

        @Override
        public ConnectionBuilder sources(final Set<Source> sources) {
            this.sources.addAll(checkNotNull(sources, "sources"));
            return this;
        }

        @Override
        public ConnectionBuilder targets(final Set<Target> targets) {
            this.targets.addAll(checkNotNull(targets, "targets"));
            return this;
        }

        @Override
        public ConnectionBuilder clientCount(final int clientCount) {
            checkArgument(clientCount, ps -> ps > 0, () -> "The client count must be > 0!");
            this.clientCount = clientCount;
            return this;
        }

        @Override
        public ConnectionBuilder specificConfig(final Map<String, String> specificConfig) {
            this.specificConfig.putAll(checkNotNull(specificConfig, "Specific Config"));
            return this;
        }

        @Override
        public ConnectionBuilder mappingContext(@Nullable final MappingContext mappingContext) {
            this.mappingContext = mappingContext;
            return this;
        }

        @Override
        public ConnectionBuilder tags(final Collection<String> tags) {
            this.tags = new HashSet<>(checkNotNull(tags, "tags to set"));
            return this;
        }

        @Override
        public ConnectionBuilder tag(final String tag) {
            tags.add(checkNotNull(tag, "tag to set"));
            return this;
        }

        @Override
        public Connection build() {
            checkSourceAndTargetAreValid();
            return new ImmutableConnection(this);
        }

        private void checkSourceAndTargetAreValid() {
            if (sources.isEmpty() && targets.isEmpty()) {
                throw ConnectionConfigurationInvalidException.newBuilder("Either a source or a target must be " +
                        "specified in the configuration of a connection!").build();
            }
        }

    }

    @Immutable
    private static final class ConnectionUri {

        private static final Pattern URI_REGEX_PATTERN = Pattern.compile(Connection.UriRegex.REGEX);

        private final String uriString;
        private final String protocol;
        @Nullable private final String userName;
        @Nullable private final String password;
        private final String hostname;
        private final int port;
        private final String path;

        private ConnectionUri(final String theUriString, final Matcher matcher) {
            uriString = theUriString;
            protocol = matcher.group(Connection.UriRegex.PROTOCOL_REGEX_GROUP);
            userName = matcher.group(Connection.UriRegex.USERNAME_REGEX_GROUP);
            password = matcher.group(Connection.UriRegex.PASSWORD_REGEX_GROUP);
            hostname = matcher.group(Connection.UriRegex.HOSTNAME_REGEX_GROUP);
            port = Integer.parseInt(matcher.group(Connection.UriRegex.PORT_REGEX_GROUP));
            path = matcher.group(Connection.UriRegex.PATH_REGEX_GROUP);
        }

        /**
         * Returns a new instance of {@code ConnectionUri}. The is the reverse function of {@link #toString()}.
         *
         * @param uriString the string representation of the Connection URI.
         * @return the instance.
         * @throws NullPointerException if {@code uriString} is {@code null}.
         * @throws org.eclipse.ditto.model.connectivity.ConnectionUriInvalidException if {@code uriString} did not
         * match {@link org.eclipse.ditto.model.connectivity.Connection.UriRegex#REGEX}.
         * @see #toString()
         */
        static ConnectionUri of(final String uriString) {
            final Matcher matcher = URI_REGEX_PATTERN.matcher(checkNotNull(uriString, "URI string"));
            if (!matcher.matches()) {
                throw ConnectionUriInvalidException.newBuilder(uriString).build();
            }
            return new ConnectionUri(uriString, matcher);
        }

        String getProtocol() {
            return protocol;
        }

        Optional<String> getUserName() {
            return Optional.ofNullable(userName);
        }

        Optional<String> getPassword() {
            return Optional.ofNullable(password);
        }

        String getHostname() {
            return hostname;
        }

        int getPort() {
            return port;
        }

        String getPath() {
            return path;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final ConnectionUri that = (ConnectionUri) o;
            return Objects.equals(uriString, that.uriString);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uriString);
        }

        /**
         * @return the string representation of this ConnectionUri. This is the reverse function of {@link #of(String)}.
         * @see #of(String)
         */
        @Override
        public String toString() {
            return uriString;
        }

    }

}
