package com.willfp.libreforge.integrations.custom_blocks.nexo

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.custom_blocks.nexo.impl.TriggerNexoBlockItemDrop

object NexoIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        plugin.eventManager.registerListener(TriggerNexoBlockItemDrop)
    }

    override fun getPluginName(): String {
        return "Nexo"
    }
}
