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
import org.eclipse.ditto.model.things.Feature;
import org.eclipse.ditto.model.things.Thing;
import org.eclipse.ditto.signals.commands.things.modify.ModifyFeatureDefinition;
import org.eclipse.ditto.signals.commands.things.modify.ModifyFeatureDefinitionResponse;
import org.eclipse.ditto.signals.events.things.FeatureDefinitionCreated;
import org.eclipse.ditto.signals.events.things.FeatureDefinitionModified;

/**
 * This strategy handles the {@link org.eclipse.ditto.signals.commands.things.modify.ModifyFeatureDefinition} command.
 */
@Immutable
final class ModifyFeatureDefinitionStrategy extends AbstractCommandStrategy<ModifyFeatureDefinition> {

    /**
     * Constructs a new {@code ModifyFeatureDefinitionStrategy} object.
     */
    ModifyFeatureDefinitionStrategy() {
        super(ModifyFeatureDefinition.class);
    }

    @Override
    protected Result doApply(final Context context, final ModifyFeatureDefinition command) {
        final Thing thing = context.getThingOrThrow();
        final String featureId = command.getFeatureId();

        return thing.getFeatures()
                .flatMap(features -> features.getFeature(featureId))
                .map(feature -> getModifyOrCreateResult(feature, context, command))
                .orElseGet(() -> ResultFactory.newResult(
                        ExceptionFactory.featureNotFound(context.getThingId(), featureId, command.getDittoHeaders())));
    }

    private static Result getModifyOrCreateResult(final Feature feature, final Context context,
            final ModifyFeatureDefinition command) {

        return feature.getDefinition()
                .map(definition -> getModifyResult(context, command))
                .orElseGet(() -> getCreateResult(context, command));
    }

    private static Result getModifyResult(final Context context, final ModifyFeatureDefinition command) {
        final String thingId = context.getThingId();
        final DittoHeaders dittoHeaders = command.getDittoHeaders();
        final String featureId = command.getFeatureId();

        return ResultFactory.newResult(FeatureDefinitionModified.of(thingId, featureId, command.getDefinition(),
                context.getNextRevision(), getEventTimestamp(), dittoHeaders),
                ModifyFeatureDefinitionResponse.modified(thingId, featureId, dittoHeaders));
    }

    private static Result getCreateResult(final Context context, final ModifyFeatureDefinition command) {
        final String thingId = context.getThingId();
        final DittoHeaders dittoHeaders = command.getDittoHeaders();
        final String featureId = command.getFeatureId();

        return ResultFactory.newResult(FeatureDefinitionCreated.of(thingId, featureId, command.getDefinition(),
                context.getNextRevision(), getEventTimestamp(), dittoHeaders),
                ModifyFeatureDefinitionResponse.created(thingId, featureId, command.getDefinition(),
                        dittoHeaders));
    }

}
