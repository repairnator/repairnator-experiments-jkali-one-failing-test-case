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
package org.eclipse.ditto.services.things.persistence.actors.strategies.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.ditto.model.things.TestConstants.Thing.THING_ID;
import static org.eclipse.ditto.model.things.TestConstants.Thing.THING_V2;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonFieldSelector;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.base.json.FieldType;
import org.eclipse.ditto.model.base.json.JsonSchemaVersion;
import org.eclipse.ditto.services.models.things.commands.sudo.SudoRetrieveThing;
import org.eclipse.ditto.services.models.things.commands.sudo.SudoRetrieveThingResponse;
import org.eclipse.ditto.signals.commands.things.exceptions.ThingNotAccessibleException;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link SudoRetrieveThingStrategy}.
 */
public final class SudoRetrieveThingStrategyTest extends AbstractCommandStrategyTest {

    private SudoRetrieveThingStrategy underTest;

    @Before
    public void setUp() {
        underTest = new SudoRetrieveThingStrategy();
    }

    @Test
    public void assertImmutability() {
        assertInstancesOf(SudoRetrieveThingStrategy.class, areImmutable());
    }

    @Test
    public void isNotDefinedForDeviantThingIds() {
        final CommandStrategy.Context context = getDefaultContext(THING_V2);
        final SudoRetrieveThing command = SudoRetrieveThing.of("org.example:myThing", DittoHeaders.empty());

        final boolean defined = underTest.isDefined(context, command);

        assertThat(defined).isFalse();
    }

    @Test
    public void isNotDefinedIfContextHasNoThing() {
        final CommandStrategy.Context context = getDefaultContext(null);
        final SudoRetrieveThing command = SudoRetrieveThing.of(THING_ID, DittoHeaders.empty());

        final boolean defined = underTest.isDefined(context, command);

        assertThat(defined).isFalse();
    }

    @Test
    public void isDefinedIfContextHasThingAndThingIdsAreEqual() {
        final CommandStrategy.Context context = getDefaultContext(THING_V2);
        final SudoRetrieveThing command = SudoRetrieveThing.of(context.getThingId(), DittoHeaders.empty());

        final boolean defined = underTest.isDefined(context, command);

        assertThat(defined).isTrue();
    }

    @Test
    public void retrieveThingWithoutSelectedFields() {
        final CommandStrategy.Context context = getDefaultContext(THING_V2);
        final DittoHeaders dittoHeaders = DittoHeaders.newBuilder()
                .schemaVersion(JsonSchemaVersion.V_1)
                .build();
        final SudoRetrieveThing command = SudoRetrieveThing.of(context.getThingId(), dittoHeaders);
        final JsonObject expectedThingJson = THING_V2.toJson(command.getImplementedSchemaVersion(),
                FieldType.regularOrSpecial());

        final CommandStrategy.Result result = underTest.doApply(context, command);

        assertThat(result.getEventToPersist()).isEmpty();
        assertThat(result.getCommandResponse()).contains(SudoRetrieveThingResponse.of(expectedThingJson, dittoHeaders));
        assertThat(result.getException()).isEmpty();
        assertThat(result.isBecomeDeleted()).isFalse();
    }

    @Test
    public void retrieveThingWithoutSelectedFieldsWithOriginalSchemaVersion() {
        final CommandStrategy.Context context = getDefaultContext(THING_V2);
        final SudoRetrieveThing command =
                SudoRetrieveThing.withOriginalSchemaVersion(context.getThingId(), DittoHeaders.empty());
        final JsonObject expectedThingJson = THING_V2.toJson(THING_V2.getImplementedSchemaVersion(),
                FieldType.regularOrSpecial());

        final CommandStrategy.Result result = underTest.doApply(context, command);

        assertThat(result.getEventToPersist()).isEmpty();
        assertThat(result.getCommandResponse()).contains(
                SudoRetrieveThingResponse.of(expectedThingJson, DittoHeaders.empty()));
        assertThat(result.getException()).isEmpty();
        assertThat(result.isBecomeDeleted()).isFalse();
    }

    @Test
    public void retrieveThingWithSelectedFields() {
        final CommandStrategy.Context context = getDefaultContext(THING_V2);
        final JsonFieldSelector fieldSelector = JsonFactory.newFieldSelector("/attribute/location");
        final DittoHeaders dittoHeaders = DittoHeaders.newBuilder()
                .schemaVersion(JsonSchemaVersion.V_1)
                .build();
        final SudoRetrieveThing command =
                SudoRetrieveThing.of(context.getThingId(), fieldSelector, dittoHeaders);
        final JsonObject expectedThingJson = THING_V2.toJson(command.getImplementedSchemaVersion(), fieldSelector,
                FieldType.regularOrSpecial());

        final CommandStrategy.Result result = underTest.doApply(context, command);

        assertThat(result.getEventToPersist()).isEmpty();
        assertThat(result.getCommandResponse()).contains(SudoRetrieveThingResponse.of(expectedThingJson, dittoHeaders));
        assertThat(result.getException()).isEmpty();
        assertThat(result.isBecomeDeleted()).isFalse();
    }

    @Test
    public void retrieveThingWithSelectedFieldsWithOriginalSchemaVersion() {
        final CommandStrategy.Context context = getDefaultContext(THING_V2);
        final JsonFieldSelector fieldSelector = JsonFactory.newFieldSelector("/attribute/location");
        final SudoRetrieveThing command =
                SudoRetrieveThing.of(context.getThingId(), fieldSelector, DittoHeaders.empty());
        final JsonObject expectedThingJson = THING_V2.toJson(THING_V2.getImplementedSchemaVersion(), fieldSelector,
                FieldType.regularOrSpecial());

        final CommandStrategy.Result result = underTest.doApply(context, command);

        assertThat(result.getEventToPersist()).isEmpty();
        assertThat(result.getCommandResponse()).contains(
                SudoRetrieveThingResponse.of(expectedThingJson, DittoHeaders.empty()));
        assertThat(result.getException()).isEmpty();
        assertThat(result.isBecomeDeleted()).isFalse();
    }

    @Test
    public void unhandledReturnsThingNotAccessibleException() {
        final CommandStrategy.Context context = getDefaultContext(THING_V2);
        final SudoRetrieveThing command = SudoRetrieveThing.of(context.getThingId(), DittoHeaders.empty());

        final CommandStrategy.Result result = underTest.unhandled(context, command);

        assertThat(result.getEventToPersist()).isEmpty();
        assertThat(result.getCommandResponse()).isEmpty();
        assertThat(result.getException()).contains(
                new ThingNotAccessibleException(context.getThingId(), command.getDittoHeaders()));
        assertThat(result.isBecomeDeleted()).isFalse();
    }

}