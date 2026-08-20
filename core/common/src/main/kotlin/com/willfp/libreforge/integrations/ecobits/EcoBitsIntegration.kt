package com.willfp.libreforge.integrations.ecobits

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.ecobits.impl.TriggerGainCurrency
import com.willfp.libreforge.triggers.Triggers

object EcoBitsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerGainCurrency)
    }

    override fun getPluginName(): String {
        return "EcoBits"
    }
}
