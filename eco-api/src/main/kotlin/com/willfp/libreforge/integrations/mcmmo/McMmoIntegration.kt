package com.willfp.libreforge.integrations.mcmmo

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.effects.Effect

object McMmoIntegration : Integration {
    private lateinit var SKILL_XP_MULTIPLIER: Effect

    fun load() {
        SKILL_XP_MULTIPLIER = EffectSkillXpMultiplier()
    }

    override fun getPluginName(): String {
        return "mcMMO"
    }
}