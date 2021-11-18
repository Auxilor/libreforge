package com.willfp.libreforge.internal.conditions

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.conditions.Condition
import com.willfp.libreforge.api.effects.ConfigViolation
import com.willfp.libreforge.api.provider.LibReforgeProviders
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerMoveEvent

class ConditionBelowY: Condition("below_y") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: PlayerMoveEvent) {
        val player = event.player

        if (event.from.y == event.to.y) {
            return
        }

        LibReforgeProviders.updateEffects(player)
    }

    override fun isConditionMet(player: Player, config: JSONConfig): Boolean {
        return player.location.y < config.getDouble("y")
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getDoubleOrNull("y")
            ?: violations.add(
                ConfigViolation(
                    "y",
                    "You must specify the y level!"
                )
            )

        return violations
    }
}