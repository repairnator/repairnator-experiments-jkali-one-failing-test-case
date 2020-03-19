/*
 * Copyright (c) 2017 Bosch Software Innovations GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/index.php
 * Contributors:
 *    Bosch Software Innovations GmbH - initial contribution
 *
 */
package org.eclipse.ditto.services.things.persistence.actors.strategies.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.NoSuchElementException;

import org.eclipse.ditto.services.things.persistence.snapshotting.ThingSnapshotter;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import akka.actor.ActorPath;
import akka.actor.RootActorPath;
import akka.event.DiagnosticLoggingAdapter;
import nl.jqno.equalsverifier.EqualsVerifier;

/**
 * Unit test for {@link DefaultContext}.
 */
public final class DefaultContextTest {

    private static final String THING_ID = "com.example.iot:myThing";
    private static final long NEXT_REVISION = 42L;
    private static final DiagnosticLoggingAdapter LOG = Mockito.mock(DiagnosticLoggingAdapter.class);
    private static final ThingSnapshotter SNAPSHOTTER = Mockito.mock(ThingSnapshotter.class);

    @Ignore("EqualsVerifier cannot cope with abstract method in ActorRef")
    @Test
    public void testHashCodeAndEquals() {
        final ActorPath red = new RootActorPath(null, "John Titor");
        final ActorPath blue = new RootActorPath(null, "");

        EqualsVerifier.forClass(DefaultContext.class)
                .usingGetClass()
                .withPrefabValues(ActorPath.class, red, blue)
                .verify();
    }

    @Test
    public void gettersForNullThing() {
        final DefaultContext underTest = DefaultContext.getInstance(THING_ID, null, NEXT_REVISION, LOG, SNAPSHOTTER);

        assertThat(underTest.getThing()).isEmpty();
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(underTest::getThingOrThrow)
                .withNoCause();
    }

}