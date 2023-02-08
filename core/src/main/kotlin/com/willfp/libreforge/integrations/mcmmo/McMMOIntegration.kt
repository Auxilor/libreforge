package com.willfp.libreforge.integrations.mcmmo

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.mcmmo.impl.EffectGiveMcMMOXp
import com.willfp.libreforge.integrations.mcmmo.impl.EffectMcMMOXpMultiplier

object McMMOIntegration : Integration {
    fun load() {
        Effects.register(EffectMcMMOXpMultiplier)
        Effects.register(EffectGiveMcMMOXp)
    }

    override fun getPluginName(): String {
        return "mcMMO"
    }
}
