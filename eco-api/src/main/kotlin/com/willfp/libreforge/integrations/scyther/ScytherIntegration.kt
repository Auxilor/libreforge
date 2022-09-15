package com.willfp.libreforge.integrations.scyther

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.triggers.Trigger

object ScytherIntegration: Integration {
    private lateinit var SCYTHER_AUTO_COLLECT: Trigger
    private lateinit var SCYTHER_AUTO_SELL: Trigger

    fun load() {
        SCYTHER_AUTO_COLLECT = TriggerScytherAutoSell()
        SCYTHER_AUTO_SELL = TriggerScytherAutoSell()
    }

    override fun getPluginName(): String {
        return "Scyther"
    }
}
