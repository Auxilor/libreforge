package com.willfp.libreforge.integrations.mcmmo

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.effects.Effect

object McMMOIntegration : Integration {
    private lateinit var MCMMO_XP_MULTIPLIER: Effect
    private lateinit var GIVE_MCMMO_XP: Effect

    fun load() {
        MCMMO_XP_MULTIPLIER = EffectMcMMOXpMultiplier()
        GIVE_MCMMO_XP = EffectGiveMcMMOXp()
    }

    override fun getPluginName(): String {
        return "mcMMO"
    }
}