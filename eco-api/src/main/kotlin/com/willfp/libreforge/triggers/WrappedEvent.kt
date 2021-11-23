package com.willfp.libreforge.triggers

import org.bukkit.event.Cancellable
import org.bukkit.event.Event

interface WrappedEvent<out T : Event>

interface WrappedCancellableEvent<out T> : WrappedEvent<T> where T: Event, T: Cancellable {
    var isCancelled: Boolean
}
