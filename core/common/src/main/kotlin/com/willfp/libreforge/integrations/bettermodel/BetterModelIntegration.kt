package com.willfp.libreforge.integrations.bettermodel

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.bettermodel.impl.EffectPlayAnimation
import com.willfp.libreforge.integrations.bettermodel.impl.EffectStopAnimation

object BetterModelIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectPlayAnimation)
        Effects.register(EffectStopAnimation)
    }

    override fun getPluginName(): String {
        return "BetterModel"
    }
}
