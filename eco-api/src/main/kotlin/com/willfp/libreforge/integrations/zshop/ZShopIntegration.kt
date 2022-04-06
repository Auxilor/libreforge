package com.willfp.libreforge.integrations.zshop

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.LibReforgePlugin

object ZShopIntegration : Integration {
    fun load(plugin: LibReforgePlugin) {
        plugin.eventManager.registerListener(ZShopSellMultiplierHook())
    }

    override fun getPluginName(): String {
        return "ZShop"
    }
}
