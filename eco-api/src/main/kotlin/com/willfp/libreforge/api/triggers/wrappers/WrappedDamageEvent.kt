package com.willfp.libreforge.api.triggers.wrappers

import com.willfp.libreforge.api.triggers.WrappedEvent
import org.bukkit.event.entity.EntityDamageEvent

class WrappedDamageEvent(
    private val event: EntityDamageEvent
) : WrappedEvent<EntityDamageEvent> {
    var damage: Double
        get() = event.damage
        set(value) {
            event.damage = value
        }
}