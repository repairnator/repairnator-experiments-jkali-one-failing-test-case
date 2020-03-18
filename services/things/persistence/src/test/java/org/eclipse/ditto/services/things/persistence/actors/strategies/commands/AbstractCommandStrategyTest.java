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

import static org.eclipse.ditto.model.things.TestConstants.Thing.THING_ID;

import javax.annotation.Nullable;

import org.eclipse.ditto.model.things.Thing;
import org.eclipse.ditto.services.things.persistence.snapshotting.ThingSnapshotter;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import akka.event.DiagnosticLoggingAdapter;

/**
 * Abstract base implementation for unit tests of implementations of {@link AbstractCommandStrategy}.
 */
public abstract class AbstractCommandStrategyTest {

    protected static final long NEXT_REVISION = 42L;

    protected static DiagnosticLoggingAdapter logger;
    protected static ThingSnapshotter thingSnapshotter;

    @BeforeClass
    public static void initTestConstants() {
        logger = Mockito.mock(DiagnosticLoggingAdapter.class);
        thingSnapshotter = Mockito.mock(ThingSnapshotter.class);
    }

    protected static CommandStrategy.Context getDefaultContext(@Nullable final Thing thing) {
        return DefaultContext.getInstance(THING_ID, thing, NEXT_REVISION, logger, thingSnapshotter);
    }

}
