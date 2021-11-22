package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerMoveEvent

class ConditionInAir: Condition("in_air") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: PlayerMoveEvent) {
        val player = event.player

        player.updateEffects()
    }

    override fun isConditionMet(player: Player, config: JSONConfig): Boolean {
        return player.location.block.isEmpty == config.getBool("in_air")
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getBoolOrNull("in_air")
            ?: violations.add(
                ConfigViolation(
                    "in_air",
                    "You must specify if the player must be in air on on land!"
                )
            )

        return violations
    }
}