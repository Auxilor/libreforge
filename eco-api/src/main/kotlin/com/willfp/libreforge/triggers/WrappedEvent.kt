package com.willfp.libreforge.triggers

import org.bukkit.event.Cancellable
import org.bukkit.event.Event

interface WrappedEvent<out T : Event>

interface WrappedCancellableEvent<out T> : WrappedEvent<T> where T : Event, T : Cancellable {
    var isCancelled: Boolean
}

class GenericCancellableEvent<out T>(
    private val handle: T
) : WrappedCancellableEvent<T> where T : Event, T : Cancellable {
    override var isCancelled: Boolean
        get() = handle.isCancelled
        set(value) {
            handle.isCancelled = value
        }
}
