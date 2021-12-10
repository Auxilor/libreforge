package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerMoveEvent

class ConditionInWorld: Condition("in_world") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: PlayerMoveEvent) {
        val player = event.player

        if (event.from.world == event.to.world) {
            return
        }

        player.updateEffects()
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.world.name.equals(config.getString("world"), ignoreCase = true)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getStringOrNull("world")
            ?: violations.add(
                ConfigViolation(
                    "world",
                    "You must specify the world name!"
                )
            )

        return violations
    }
}