package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionInBiome: Condition("in_biome") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return config.getStrings("biomes", false).contains(player.world.getBiome(
            player.location.blockX,
            player.location.blockY,
            player.location.blockZ
        ).name.lowercase())
    }

    override fun validateConfig(config: Config): List<com.willfp.libreforge.ConfigViolation> {
        val violations = mutableListOf<com.willfp.libreforge.ConfigViolation>()

        config.getStringsOrNull("biomes", false)
            ?: violations.add(
                com.willfp.libreforge.ConfigViolation(
                    "biomes",
                    "You must specify the biomes!"
                )
            )

        return violations
    }
}