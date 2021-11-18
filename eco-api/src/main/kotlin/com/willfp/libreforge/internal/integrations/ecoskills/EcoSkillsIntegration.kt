package com.willfp.libreforge.internal.integrations.ecoskills

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.api.effects.Effect

object EcoSkillsIntegration : Integration {
    private lateinit var ADD_STAT: Effect

    fun load() {
        ADD_STAT = EffectAddStat()
    }

    override fun getPluginName(): String {
        return "EcoSkills"
    }
}