package com.willfp.libreforge.integrations.mcmmo

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.mcmmo.impl.EffectGiveMcMMOXp
import com.willfp.libreforge.integrations.mcmmo.impl.EffectMcMMOXpMultiplier

object McMMOIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectMcMMOXpMultiplier)
        Effects.register(EffectGiveMcMMOXp)
    }

    override fun getPluginName(): String {
        return "mcMMO"
    }
}
