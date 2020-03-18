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

import org.eclipse.ditto.model.base.common.Validator;
import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.things.AccessControlList;
import org.eclipse.ditto.model.things.AclValidator;
import org.eclipse.ditto.model.things.Thing;
import org.eclipse.ditto.signals.commands.things.modify.ModifyAcl;
import org.eclipse.ditto.signals.commands.things.modify.ModifyAclResponse;
import org.eclipse.ditto.signals.events.things.AclModified;

/**
 * This strategy handles the {@link ModifyAcl} command.
 */
@Immutable
public final class ModifyAclStrategy extends AbstractCommandStrategy<ModifyAcl> {

    /**
     * Constructs a new {@code ModifyAclStrategy} object.
     */
    ModifyAclStrategy() {
        super(ModifyAcl.class);
    }

    @Override
    protected Result doApply(final Context context, final ModifyAcl command) {
        final String thingId = context.getThingId();
        final AccessControlList newAccessControlList = command.getAccessControlList();
        final DittoHeaders dittoHeaders = command.getDittoHeaders();

        final Validator aclValidator = AclValidator.newInstance(newAccessControlList,
                Thing.MIN_REQUIRED_PERMISSIONS);
        if (!aclValidator.isValid()) {
            return ResultFactory.newResult(ExceptionFactory.aclInvalid(thingId, aclValidator.getReason(),
                    dittoHeaders));
        }

        return ResultFactory.newResult(AclModified.of(thingId, newAccessControlList, context.getNextRevision(),
                getEventTimestamp(), dittoHeaders),
                ModifyAclResponse.modified(thingId, newAccessControlList, command.getDittoHeaders()));
    }

}
