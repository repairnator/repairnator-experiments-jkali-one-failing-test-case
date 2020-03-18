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
package org.eclipse.ditto.services.things.starter;

import static org.eclipse.ditto.model.base.common.ConditionChecker.checkNotNull;

import java.util.Map;

import org.eclipse.ditto.services.base.DittoService;
import org.eclipse.ditto.services.base.config.DittoServiceConfigReader;
import org.eclipse.ditto.services.base.config.ServiceConfigReader;
import org.eclipse.ditto.services.base.metrics.MongoDbMetricRegistryFactory;
import org.eclipse.ditto.services.base.metrics.StatsdMetricsReporter;
import org.eclipse.ditto.services.base.metrics.StatsdMetricsStarter;
import org.eclipse.ditto.services.things.persistence.snapshotting.ThingSnapshotter;
import org.slf4j.Logger;

import com.codahale.metrics.MetricRegistry;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.ActorMaterializer;

/**
 * Abstract base implementation for starting Things service with configurable actors.
 * <ul>
 * <li>Reads configuration, enhances it with cloud environment settings.</li>
 * <li>Sets up Akka actor system.</li>
 * <li>Wires up Akka HTTP Routes.</li>
 * </ul>
 */
public abstract class AbstractThingsService extends DittoService<ServiceConfigReader> {

    /**
     * Name for the Akka Actor System of the Things service.
     */
    private static final String SERVICE_NAME = "things";

    private final ThingSnapshotter.Create thingSnapshotterCreate;

    /**
     * Constructs a new {@code AbstractThingsService} object.
     *
     * @param logger the logger to be used.
     * @param thingSnapshotterCreate functional interface for the constructor of snapshotter classes.
     * @throws NullPointerException if any argument is {@code null}.
     */
    protected AbstractThingsService(final Logger logger, final ThingSnapshotter.Create thingSnapshotterCreate) {
        super(logger, SERVICE_NAME, ThingsRootActor.ACTOR_NAME, DittoServiceConfigReader.from(SERVICE_NAME));

        this.thingSnapshotterCreate = checkNotNull(thingSnapshotterCreate);
    }

    @Override
    protected void startStatsdMetricsReporter(final ActorSystem actorSystem, final ServiceConfigReader configReader) {
        final Map.Entry<String, MetricRegistry> mongoDbMetrics =
                MongoDbMetricRegistryFactory.createOrGet(actorSystem, configReader.getRawConfig());
        StatsdMetricsReporter.getInstance().add(mongoDbMetrics);

        StatsdMetricsStarter.newInstance(configReader, actorSystem, SERVICE_NAME).run();
    }

    @Override
    protected Props getMainRootActorProps(final ServiceConfigReader configReader, final ActorRef pubSubMediator,
            final ActorMaterializer materializer) {

        return ThingsRootActor.props(configReader, pubSubMediator, materializer, thingSnapshotterCreate);
    }

}
