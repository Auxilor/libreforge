package com.willfp.libreforge.integrations.jobs

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.jobs.impl.EffectJobsMoneyMultiplier
import com.willfp.libreforge.integrations.jobs.impl.EffectJobsXpMultiplier
import com.willfp.libreforge.integrations.jobs.impl.TriggerJobsLevelUp
import com.willfp.libreforge.triggers.Triggers

object JobsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectJobsMoneyMultiplier)
        Effects.register(EffectJobsXpMultiplier)
        Triggers.register(TriggerJobsLevelUp)
    }

    override fun getPluginName(): String {
        return "Jobs"
    }
}
