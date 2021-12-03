package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent

class ConditionBelowHealthPercent: Condition("below_health_percent") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: EntityRegainHealthEvent) {
        val player = event.entity

        if (player !is Player) {
            return
        }

        player.updateEffects()
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

        player.updateEffects()
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: return false
        val health = player.health

        return health / maxHealth * 100 <= config.getDouble("percent")
    }

    override fun validateConfig(config: Config): List<com.willfp.libreforge.ConfigViolation> {
        val violations = mutableListOf<com.willfp.libreforge.ConfigViolation>()

        config.getDoubleOrNull("percent")
            ?: violations.add(
                com.willfp.libreforge.ConfigViolation(
                    "percent",
                    "You must specify the health percentage!"
                )
            )

        return violations
    }
}