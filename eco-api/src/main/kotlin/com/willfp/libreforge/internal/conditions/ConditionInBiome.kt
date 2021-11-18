package com.willfp.libreforge.internal.conditions

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.conditions.Condition
import com.willfp.libreforge.api.effects.ConfigViolation
import com.willfp.libreforge.api.provider.LibReforgeProviders
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerMoveEvent

class ConditionInBiome: Condition("in_biome") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: PlayerMoveEvent) {
        val player = event.player

        if (event.from.world?.getBiome(event.from) == event.to.world?.getBiome(event.to)) {
            return
        }

        LibReforgeProviders.updateEffects(player)
    }

    override fun isConditionMet(player: Player, config: JSONConfig): Boolean {
        return config.getStrings("biomes", false).contains(player.world.getBiome(
            player.location.blockX,
            player.location.blockY,
            player.location.blockZ
        ).name.lowercase())
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getStringsOrNull("biomes", false)
            ?: violations.add(
                ConfigViolation(
                    "biomes",
                    "You must specify the biomes!"
                )
            )

        return violations
    }
}