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

import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.things.Feature;
import org.eclipse.ditto.model.things.Thing;
import org.eclipse.ditto.signals.commands.things.modify.DeleteFeatureProperty;
import org.eclipse.ditto.signals.commands.things.modify.DeleteFeaturePropertyResponse;
import org.eclipse.ditto.signals.events.things.FeaturePropertyDeleted;

/**
 * This strategy handles the {@link org.eclipse.ditto.signals.commands.things.modify.DeleteFeatureProperty} command.
 */
@Immutable
final class DeleteFeaturePropertyStrategy extends AbstractCommandStrategy<DeleteFeatureProperty> {

    /**
     * Constructs a new {@code DeleteFeaturePropertyStrategy} object.
     */
    DeleteFeaturePropertyStrategy() {
        super(DeleteFeatureProperty.class);
    }

    @Override
    protected Result doApply(final Context context, final DeleteFeatureProperty command) {
        final Thing thing = context.getThingOrThrow();

        return thing.getFeatures()
                .flatMap(features -> features.getFeature(command.getFeatureId()))
                .map(feature -> getDeleteFeaturePropertyResult(feature, context, command))
                .orElseGet(() -> ResultFactory.newResult(
                        ExceptionFactory.featureNotFound(context.getThingId(), command.getFeatureId(),
                                command.getDittoHeaders())));
    }

    private static Result getDeleteFeaturePropertyResult(final Feature feature, final Context context,
            final DeleteFeatureProperty command) {

        final JsonPointer propertyPointer = command.getPropertyPointer();
        final String thingId = context.getThingId();
        final String featureId = command.getFeatureId();
        final DittoHeaders dittoHeaders = command.getDittoHeaders();

        return feature.getProperties()
                .flatMap(featureProperties -> featureProperties.getValue(propertyPointer))
                .map(featureProperty -> ResultFactory.newResult(
                        FeaturePropertyDeleted.of(thingId, featureId, propertyPointer, context.getNextRevision(),
                                getEventTimestamp(), dittoHeaders),
                        DeleteFeaturePropertyResponse.of(thingId, featureId, propertyPointer, dittoHeaders)))
                .orElseGet(() -> ResultFactory.newResult(
                        ExceptionFactory.featurePropertyNotFound(thingId, featureId, propertyPointer, dittoHeaders)));
    }

}
