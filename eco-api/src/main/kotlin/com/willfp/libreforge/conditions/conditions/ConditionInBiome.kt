package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
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