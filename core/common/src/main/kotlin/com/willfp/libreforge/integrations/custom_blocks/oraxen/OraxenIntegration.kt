package com.willfp.libreforge.integrations.custom_blocks.oraxen

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.custom_blocks.oraxen.impl.TriggerOraxenBlockItemDrop

object OraxenIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        plugin.eventManager.registerListener(TriggerOraxenBlockItemDrop)
    }

    override fun getPluginName(): String {
        return "Oraxen"
    }
}
