package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.weather.WeatherChangeEvent

class ConditionIsStorm: Condition("is_storm") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: WeatherChangeEvent) {
        for (player in event.world.players) {
            player.updateEffects(noRescan = true)
        }
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.world.hasStorm() == config.getBool("is_storm")
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("is_storm")) violations.add(
                ConfigViolation(
                    "is_storm",
                    "You must specify if the player must be in a storm or not!"
                )
            )

        return violations
    }
}