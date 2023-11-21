package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition

object ConditionInBiome : Condition<NoCompileData>("in_biome") {
    override val arguments = arguments {
        require("biomes", "You must specify the biomes!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val location = dispatcher.location ?: return false
        return config.getStrings("biomes").contains(
            location.world.getBiome(
                location.blockX,
                location.blockY,
                location.blockZ
            ).name.lowercase()
        )
    }
}
