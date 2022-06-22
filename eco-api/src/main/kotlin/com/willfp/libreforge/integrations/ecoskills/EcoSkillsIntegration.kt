package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.Trigger

object EcoSkillsIntegration : Integration {
    private lateinit var ADD_STAT: Effect
    private lateinit var MULTIPLY_STAT: Effect
    private lateinit var HAS_SKILL_LEVEL: Condition
    private lateinit var SKILL_XP_MULTIPLIER: Effect
    private lateinit var GAIN_SKILL_XP: Trigger
    private lateinit var GIVE_SKILL_XP: Effect
    private lateinit var LEVEL_UP_SKILL: Trigger
    private lateinit var MULTIPLY_ALL_STATS: Effect
    private lateinit var ADD_STAT_TEMPORARILY: Effect
    private lateinit var MULTIPLY_STAT_TEMPORARILY: Effect
    private lateinit var SKILL: FilterComponent

    fun load() {
        ADD_STAT = EffectAddStat()
        MULTIPLY_STAT = EffectMultiplyStat()
        HAS_SKILL_LEVEL = ConditionHasSkillLevel()
        SKILL_XP_MULTIPLIER = EffectSkillXpMultiplier()
        GAIN_SKILL_XP = TriggerGainSkillXp()
        LEVEL_UP_SKILL = TriggerLevelUpSkill()
        GIVE_SKILL_XP = EffectGiveSkillXp()
        MULTIPLY_ALL_STATS = EffectMultiplyAllStats()
        ADD_STAT_TEMPORARILY = EffectAddStatTemporarily()
        MULTIPLY_STAT_TEMPORARILY = MultiplyStatTemporarily()
        SKILL = FilterSkill()
    }

    override fun getPluginName(): String {
        return "EcoSkills"
    }
}