/*
 * Copyright (c) 2010-2016. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.eventhandling.saga.metamodel;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.saga.AssociationValue;
import org.axonframework.eventhandling.saga.SagaMethodMessageHandlingMember;

import java.util.List;
import java.util.Optional;

/**
 * Interface of a model that describes a Saga of type {@code T}. Use the SagaModel to obtain associations and
 * event handlers for the Saga.
 *
 * @param <T> The saga type
 */
public interface SagaModel<T> {

    /**
     * Returns the {@link AssociationValue} used to find sagas of type {@code T} that can handle the given {@code
     * eventMessage}. If the saga type does not handle events of this type an empty Optional is returned.
     *
     * @param eventMessage The event to find the association value for
     * @return Optional of the AssociationValue for the event, or an empty Optional if the saga doesn't handle the event
     */
    Optional<AssociationValue> resolveAssociation(EventMessage<?> eventMessage);

    /**
     * Returns a List of {@link SagaMethodMessageHandlingMember event handlers} that can handle the given event.
     *
     * @param event The event to process
     * @return event message handlers for this event
     */
    List<SagaMethodMessageHandlingMember<T>> findHandlerMethods(EventMessage<?> event);

    /**
     * Indicates whether the Saga described by this model has a handler for the given {@code eventMessage}
     *
     * @param eventMessage The message to check the availability of a handler for
     * @return {@code true} if there the Saga has a handler for this message, otherwise {@code false}
     */
    default boolean hasHandlerMethod(EventMessage<?> eventMessage) {
        return !findHandlerMethods(eventMessage).isEmpty();
    }

    /**
     * Returns the factory that created this model.
     *
     * @return The factory that made this model
     */
    SagaMetaModelFactory modelFactory();
}
