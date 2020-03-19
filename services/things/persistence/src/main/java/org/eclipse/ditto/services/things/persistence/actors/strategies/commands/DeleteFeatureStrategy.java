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

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.things.Thing;
import org.eclipse.ditto.signals.commands.things.modify.DeleteFeature;
import org.eclipse.ditto.signals.commands.things.modify.DeleteFeatureResponse;
import org.eclipse.ditto.signals.events.things.FeatureDeleted;

/**
 * This strategy handles the {@link org.eclipse.ditto.signals.commands.things.modify.DeleteFeature} command.
 */
@Immutable
final class DeleteFeatureStrategy extends AbstractCommandStrategy<DeleteFeature> {

    /**
     * Constructs a new {@code DeleteFeatureStrategy} object.
     */
    DeleteFeatureStrategy() {
        super(DeleteFeature.class);
    }

    @Override
    protected Result doApply(final Context context, final DeleteFeature command) {
        final Thing thing = context.getThingOrThrow();
        final String featureId = command.getFeatureId();

        return thing.getFeatures()
                .flatMap(features -> features.getFeature(featureId))
                .map(feature -> getDeleteFeatureResult(context, command))
                .orElseGet(() -> ResultFactory.newResult(
                        ExceptionFactory.featureNotFound(context.getThingId(), featureId, command.getDittoHeaders())));
    }

    private static Result getDeleteFeatureResult(final Context context, final DeleteFeature command) {
        final String thingId = context.getThingId();
        final String featureId = command.getFeatureId();
        final DittoHeaders dittoHeaders = command.getDittoHeaders();

        return ResultFactory.newResult(
                FeatureDeleted.of(thingId, featureId, context.getNextRevision(), getEventTimestamp(), dittoHeaders),
                DeleteFeatureResponse.of(thingId, featureId, dittoHeaders));
    }

}
