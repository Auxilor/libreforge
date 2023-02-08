package com.willfp.libreforge.integrations.jobs

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.jobs.impl.EffectJobsMoneyMultiplier
import com.willfp.libreforge.integrations.jobs.impl.EffectJobsXpMultiplier
import com.willfp.libreforge.integrations.jobs.impl.TriggerJobsLevelUp
import com.willfp.libreforge.triggers.Triggers

object JobsIntegration : Integration {
    fun load() {
        Effects.register(EffectJobsXpMultiplier)
        Effects.register(EffectJobsMoneyMultiplier)
        Triggers.register(TriggerJobsLevelUp)
    }

    override fun getPluginName(): String {
        return "Jobs"
    }
}
