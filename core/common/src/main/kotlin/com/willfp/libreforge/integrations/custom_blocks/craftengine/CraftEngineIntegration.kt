package com.willfp.libreforge.integrations.custom_blocks.craftengine

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.custom_blocks.craftengine.impl.EffectCraftEngineTelekinesis

object CraftEngineIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectCraftEngineTelekinesis)
    }

    override fun getPluginName(): String {
        return "CraftEngine"
    }
}
