package com.willfp.libreforge.triggers.wrappers

import com.willfp.libreforge.triggers.WrappedCancellableEvent
import org.bukkit.event.entity.PlayerDeathEvent

class WrappedDeathEvent(
    private val event: PlayerDeathEvent
) : WrappedCancellableEvent<PlayerDeathEvent> {
    override var isCancelled: Boolean
        get() {
            return event.isCancelled
        }
        set(value) {
            event.isCancelled = value
        }
    var keepInventory: Boolean
        get() {
            return event.keepInventory
        }
        set(value) {
            event.keepInventory = value
        }
}
