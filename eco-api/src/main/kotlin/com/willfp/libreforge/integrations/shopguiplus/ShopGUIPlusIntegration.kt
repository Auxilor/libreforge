package com.willfp.libreforge.integrations.shopguiplus

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.LibReforgePlugin

object ShopGUIPlusIntegration : Integration {
    fun load(plugin: LibReforgePlugin) {
        plugin.eventManager.registerListener(ShopGUIPlusSellMultiplierHook())
    }

    override fun getPluginName(): String {
        return "ShopGUIPlus"
    }
}
