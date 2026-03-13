package com.willfp.libreforge.integrations.custom_blocks.nexo

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.custom_blocks.nexo.impl.EffectNexoTelekinesis

object NexoIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectNexoTelekinesis)
    }

    override fun getPluginName(): String {
        return "Nexo"
    }
}
