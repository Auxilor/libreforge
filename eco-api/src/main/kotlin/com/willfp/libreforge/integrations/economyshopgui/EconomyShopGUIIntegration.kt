package com.willfp.libreforge.integrations.economyshopgui

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.LibReforgePlugin

object EconomyShopGUIIntegration : Integration {
    fun load(plugin: LibReforgePlugin) {
        plugin.eventManager.registerListener(EconomyShopGUISellMultiplierHook())
    }

    override fun getPluginName(): String {
        return "EconomyShopGUI"
    }
}
