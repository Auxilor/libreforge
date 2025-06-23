package com.willfp.libreforge.integrations.AxPlugins.axtrade

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.integrations.AxPlugins.axtrade.impl.TriggerPlayerTrade
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.triggers.Triggers

object AxTradeIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerPlayerTrade)
    }

    override fun getPluginName(): String {
        return "AxTrade"
    }
}
