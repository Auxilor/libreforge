package com.willfp.libreforge.triggers.wrappers

import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

class WrappedMeleeDamageEvent(
    private val event: EntityDamageByEntityEvent
) : WrappedDamageEvent(event) {
    val isFullyCharged: Boolean
        get() {
            val damager = event.damager as? Player ?: return true
            return damager.attackCooldown >= 1f
        }
}
