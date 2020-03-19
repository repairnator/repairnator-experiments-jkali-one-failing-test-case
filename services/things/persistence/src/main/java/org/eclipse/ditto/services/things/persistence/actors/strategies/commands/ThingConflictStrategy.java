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

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.signals.commands.things.exceptions.ThingConflictException;
import org.eclipse.ditto.signals.commands.things.modify.CreateThing;

/**
 * This strategy handles the {@link CreateThing} command for an already existing Thing.
 */
@Immutable
final class ThingConflictStrategy extends AbstractCommandStrategy<CreateThing> {

    /**
     * Constructs a new {@code ThingConflictStrategy} object.
     */
    ThingConflictStrategy() {
        super(CreateThing.class);
    }

    @Override
    public boolean isDefined(final Context context, final CreateThing command) {
        return Objects.equals(context.getThingId(), command.getId());
    }

    @Override
    protected CommandStrategy.Result doApply(final CommandStrategy.Context context, final CreateThing command) {
        return ResultFactory.newResult(ThingConflictException.newBuilder(command.getId())
                .dittoHeaders(command.getDittoHeaders())
                .build());
    }

}