package com.willfp.libreforge.triggers.wrappers

import com.destroystokyo.paper.event.block.BeaconEffectEvent
import com.willfp.libreforge.triggers.WrappedCancellableEvent
import org.bukkit.event.entity.PlayerDeathEvent

class WrappedPlayerDeathEvent(private val event: PlayerDeathEvent): WrappedCancellableEvent<BeaconEffectEvent> {
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
