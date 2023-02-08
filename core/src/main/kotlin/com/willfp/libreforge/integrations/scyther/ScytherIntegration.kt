package com.willfp.libreforge.integrations.scyther

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.integrations.scyther.impl.TriggerScytherAutoCollect
import com.willfp.libreforge.integrations.scyther.impl.TriggerScytherAutoSell
import com.willfp.libreforge.triggers.Triggers

object ScytherIntegration : Integration {
    fun load() {
        Triggers.register(TriggerScytherAutoCollect)
        Triggers.register(TriggerScytherAutoSell)
    }

    override fun getPluginName(): String {
        return "Scyther"
    }
}
