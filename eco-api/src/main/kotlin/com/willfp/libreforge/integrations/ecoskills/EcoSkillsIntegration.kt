package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.effects.Effect

object EcoSkillsIntegration : Integration {
    private lateinit var ADD_STAT: Effect

    fun load() {
        ADD_STAT = EffectAddStat()
    }

    override fun getPluginName(): String {
        return "EcoSkills"
    }
}