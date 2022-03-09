package com.willfp.libreforge.triggers.wrappers

import com.willfp.libreforge.triggers.WrappedCancellableEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.util.Vector

class WrappedShootBowEvent(private val event: EntityShootBowEvent) : WrappedCancellableEvent<BlockDamageEvent> {
    override var isCancelled: Boolean
        get() {
            return event.isCancelled
        }
        set(value) {
            event.isCancelled = value
        }

    var velocity: Vector
        get() {
            return event.projectile.velocity
        }
        set(value) {
            event.projectile.velocity = value
        }

    var hasFire: Boolean
        get() {
            return event.projectile.fireTicks > 0
        }
        set(value) {
            if (value) {
                event.projectile.fireTicks = Int.MAX_VALUE
            } else {
                event.projectile.fireTicks = 0
            }
        }
}
