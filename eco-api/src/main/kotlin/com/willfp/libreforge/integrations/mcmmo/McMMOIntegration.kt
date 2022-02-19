package com.willfp.libreforge.integrations.mcmmo

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.effects.Effect

object McMMOIntegration : Integration {
    private lateinit var SKILL_XP_MULTIPLIER: Effect
    private lateinit var GIVE_SKILL_XP: Effect

    fun load() {
        SKILL_XP_MULTIPLIER = EffectSkillXpMultiplier()
        GIVE_SKILL_XP = EffectGiveSkillXp()
    }

    override fun getPluginName(): String {
        return "mcMMO"
    }
}