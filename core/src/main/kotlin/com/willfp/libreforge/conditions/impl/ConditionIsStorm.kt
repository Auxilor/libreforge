package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.weather.WeatherChangeEvent

object ConditionIsStorm: Condition<NoCompileData>("is_storm") {
    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return player.world.hasStorm() == (config.getBoolOrNull("is_storm") ?: true) // Legacy
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: WeatherChangeEvent) {
        for (player in event.world.players) {
            player.updateEffects()
        }
    }
}
