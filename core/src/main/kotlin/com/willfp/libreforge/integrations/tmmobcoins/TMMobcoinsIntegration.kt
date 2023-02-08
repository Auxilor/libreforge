package com.willfp.libreforge.integrations.tmmobcoins

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.jobs.impl.EffectJobsMoneyMultiplier
import com.willfp.libreforge.integrations.jobs.impl.EffectJobsXpMultiplier

object TMMobcoinsIntegration : Integration {
    fun load() {
        Effects.register(EffectJobsXpMultiplier)
        Effects.register(EffectJobsMoneyMultiplier)
    }

    override fun getPluginName(): String {
        return "TMMobcoins"
    }
}
