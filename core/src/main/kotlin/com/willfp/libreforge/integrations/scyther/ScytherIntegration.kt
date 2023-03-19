package com.willfp.libreforge.integrations.scyther

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.scyther.impl.TriggerScytherAutoCollect
import com.willfp.libreforge.integrations.scyther.impl.TriggerScytherAutoSell
import com.willfp.libreforge.triggers.Triggers

object ScytherIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerScytherAutoCollect)
        Triggers.register(TriggerScytherAutoSell)
    }

    override fun getPluginName(): String {
        return "Scyther"
    }
}
