package com.willfp.libreforge.integrations.ecobits

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.ecobits.impl.EcoBitsCurrencyGainListener

object EcoBitsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        plugin.eventManager.registerListener(EcoBitsCurrencyGainListener)
    }

    override fun getPluginName(): String {
        return "EcoBits"
    }
}
