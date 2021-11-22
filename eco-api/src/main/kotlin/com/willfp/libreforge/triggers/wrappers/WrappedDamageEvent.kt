package com.willfp.libreforge.triggers.wrappers

import com.willfp.libreforge.triggers.WrappedEvent
import org.bukkit.event.entity.EntityDamageEvent

class WrappedDamageEvent(
    private val event: EntityDamageEvent
) : WrappedEvent<EntityDamageEvent> {
    var damage: Double
        get() = event.damage
        set(value) {
            event.damage = value
        }

    var isCancelled: Boolean
        get() {
            return event.isCancelled
        }
        set(value) {
            event.isCancelled = value
        }
}
