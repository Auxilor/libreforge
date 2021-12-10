package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
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

        if (event.from.block.biome == event.to.block.biome) {
            return
        }

        player.updateEffects()
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return config.getStrings("biomes", false).contains(player.world.getBiome(
            player.location.blockX,
            player.location.blockY,
            player.location.blockZ
        ).name.lowercase())
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
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