package com.willfp.libreforge.integrations.tmmobcoins

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.jobs.EffectJobsMoneyMultiplier

object TMMobCoinsIntegration : Integration {
    private lateinit var JOBS_XP_MULTIPLIER: Effect
    private lateinit var JOBS_MONEY_MULTIPLIER: Effect

    fun load() {
        JOBS_XP_MULTIPLIER = EffectMobCoinsMultiplier()
        JOBS_MONEY_MULTIPLIER = EffectJobsMoneyMultiplier()
    }

    override fun getPluginName(): String {
        return "TMMobCoins"
    }
}
