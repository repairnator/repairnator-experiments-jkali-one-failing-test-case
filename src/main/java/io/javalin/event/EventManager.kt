/*
 * Javalin - https://javalin.io
 * Copyright 2017 David Åse
 * Licensed under Apache 2.0: https://github.com/tipsy/javalin/blob/master/LICENSE
 */

package io.javalin.event

import io.javalin.Javalin
import java.util.*

class EventManager {
    val listenerMap = EventType.values().associate { it to ArrayList<EventListener>() }
    fun fireEvent(eventType: EventType, javalin: Javalin) = listenerMap[eventType]!!.forEach { listener -> listener.handleEvent(Event(eventType, javalin)) }
}

data class Event(val eventType: EventType, val javalin: Javalin)

enum class EventType {
    SERVER_STARTING,
    SERVER_STARTED,
    SERVER_START_FAILED,
    SERVER_STOPPING,
    SERVER_STOPPED
}
