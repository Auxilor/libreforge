package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.effects.Effect

object EcoSkillsIntegration : Integration {
    private lateinit var ADD_STAT: Effect
    private lateinit var MULTIPLY_STAT: Effect
    private lateinit var HAS_SKILL_LEVEL: Condition

    fun load() {
        ADD_STAT = EffectAddStat()
        MULTIPLY_STAT = EffectMultiplyStat()
        HAS_SKILL_LEVEL = ConditionHasSkillLevel()
    }

    override fun getPluginName(): String {
        return "EcoSkills"
    }
}