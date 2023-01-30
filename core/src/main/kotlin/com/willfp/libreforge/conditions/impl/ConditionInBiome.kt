package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionInBiome : Condition<NoCompileData>("in_biome") {
    override val arguments = arguments {
        require("biomes", "You must specify the biomes!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return config.getStrings("biomes").contains(
            player.world.getBiome(
                player.location.blockX,
                player.location.blockY,
                player.location.blockZ
            ).name.lowercase()
        )
    }
}
