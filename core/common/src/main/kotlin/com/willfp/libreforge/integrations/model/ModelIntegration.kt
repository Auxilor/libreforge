package com.willfp.libreforge.integrations.model

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.model.impl.EffectPlayAnimation
import com.willfp.libreforge.integrations.model.impl.EffectStopAnimation

/** Animation effects for every model plugin eco supports; loading it for each one is harmless. */
class ModelIntegration(
    private val pluginName: String
) : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectPlayAnimation)
        Effects.register(EffectStopAnimation)
    }

    override fun getPluginName(): String = pluginName
}
