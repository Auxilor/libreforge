package com.willfp.libreforge.integrations.jobs

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.effects.Effect

object JobsIntegration : Integration {
    private lateinit var JOBS_XP_MULTIPLIER: Effect
    private lateinit var JOBS_MONEY_MULTIPLIER: Effect

    fun load() {
        JOBS_XP_MULTIPLIER = EffectJobsXpMultiplier()
        JOBS_MONEY_MULTIPLIER = EffectJobsMoneyMultiplier()
    }

    override fun getPluginName(): String {
        return "Jobs"
    }
}