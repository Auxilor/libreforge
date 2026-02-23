package com.willfp.libreforge.integrations.modelengine

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.modelengine.impl.EffectPlayAnimation

object ModelEngineIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectPlayAnimation)
    }

    override fun getPluginName(): String {
        return "ModelEngine"
    }
}
