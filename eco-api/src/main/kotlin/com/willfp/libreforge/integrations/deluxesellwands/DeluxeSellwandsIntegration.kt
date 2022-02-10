package com.willfp.libreforge.integrations.deluxesellwands

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.LibReforgePlugin

object DeluxeSellwandsIntegration : Integration {
    fun load(plugin: LibReforgePlugin) {
        plugin.eventManager.registerListener(DeluxeSellwandsSellMultiplierHook())
    }

    override fun getPluginName(): String {
        return "DeluxeSellwands"
    }
}