package com.willfp.libreforge.integrations.terraformgenerator.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.biomes.namedBiome
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

        return config.getStrings("biomes")
            .containsIgnoreCase(location.namedBiome?.name ?: return false)
    }
}
