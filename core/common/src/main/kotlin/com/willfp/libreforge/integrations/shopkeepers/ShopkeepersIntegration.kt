package com.willfp.libreforge.integrations.shopkeepers

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.shopkeepers.impl.TriggerShopkeepersTrade
import com.willfp.libreforge.triggers.Triggers

object ShopkeepersIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerShopkeepersTrade)
    }

    override fun getPluginName(): String {
        return "Shopkeepers"
    }
}
