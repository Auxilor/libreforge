package com.willfp.libreforge.triggers.wrappers

import com.willfp.libreforge.triggers.WrappedEvent
import org.bukkit.event.entity.EntityRegainHealthEvent

class WrappedRegenEvent(
    private val event: EntityRegainHealthEvent
) : WrappedEvent<EntityRegainHealthEvent> {
    var amount: Double
        get() = event.amount
        set(value) {
            event.amount = value
        }
}
