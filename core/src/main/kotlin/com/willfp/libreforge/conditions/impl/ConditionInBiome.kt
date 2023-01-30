package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionInBiome : Condition("in_biome") {
    override val arguments = arguments {
        require("biomes", "You must specify the biomes!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return config.getStrings("biomes").contains(
            player.world.getBiome(
                player.location.blockX,
                player.location.blockY,
                player.location.blockZ
            ).name.lowercase()
        )
    }
}
