package com.willfp.libreforge.internal.conditions

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.conditions.Condition
import com.willfp.libreforge.api.provider.LibReforgeProviders
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent

class ConditionAboveHealthPercent: Condition("above_health_percent") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: EntityRegainHealthEvent) {
        val player = event.entity

        if (player !is Player) {
            return
        }

        LibReforgeProviders.updateEffects(player)
    }

    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: EntityDamageEvent) {
        val player = event.entity

        if (player !is Player) {
            return
        }

        LibReforgeProviders.updateEffects(player)
    }

    override fun isConditionMet(player: Player, config: JSONConfig): Boolean {
        val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: return false
        val health = player.health

        return health / maxHealth * 100 >= config.getDouble("percent")
    }
}