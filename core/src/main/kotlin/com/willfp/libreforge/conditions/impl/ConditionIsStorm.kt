package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
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
        return player.world.hasStorm() == (config.getBoolOrNull("is_storm") ?: true)
    }
}