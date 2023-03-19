package com.willfp.libreforge.integrations.tmmobcoins

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.jobs.impl.EffectJobsMoneyMultiplier
import com.willfp.libreforge.integrations.jobs.impl.EffectJobsXpMultiplier

object TMMobcoinsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectJobsXpMultiplier)
        Effects.register(EffectJobsMoneyMultiplier)
    }

    override fun getPluginName(): String {
        return "TMMobcoins"
    }
}
