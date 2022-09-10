package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.Trigger

object EcoJobsIntegration : Integration {
    private lateinit var HAS_JOB_LEVEL: Condition
    private lateinit var JOB_XP_MULTIPLIER: Effect
    private lateinit var HAS_ACTIVE_JOB: Condition
    private lateinit var GAIN_JOB_XP: Trigger
    private lateinit var GIVE_JOB_XP: Effect
    private lateinit var LEVEL_UP_JOB: Trigger
    private lateinit var JOB: FilterComponent

    fun load() {
        HAS_JOB_LEVEL = ConditionHasJobLevel()
        JOB_XP_MULTIPLIER = EffectJobXpMultiplier()
        HAS_ACTIVE_JOB = ConditionHasActiveJob()
        GAIN_JOB_XP = TriggerGainJobXp()
        GIVE_JOB_XP = EffectGiveJobXp()
        LEVEL_UP_JOB = TriggerLevelUpJob()
        JOB = FilterJob()
    }

    override fun getPluginName(): String {
        return "EcoJobs"
    }
}
