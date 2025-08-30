package com.willfp.libreforge.integrations.purpur

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.purpur.impl.TriggerAnvilModify
import com.willfp.libreforge.integrations.purpur.impl.TriggerGrindItem
import com.willfp.libreforge.triggers.Triggers

object PurpurIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerGrindItem)
        Triggers.register(TriggerAnvilModify)
    }

    // I know it's not a plugin but shhhh
    override fun getPluginName(): String {
        return "Purpur"
    }
}
