package com.willfp.libreforge.integrations.customcrops

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.customcrops.impl.FilterCustomFishType
import com.willfp.libreforge.integrations.customcrops.impl.TriggerCatchFish
import com.willfp.libreforge.integrations.customcrops.impl.TriggerCatchFishFail
import com.willfp.libreforge.triggers.Triggers

object CustomFishingIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Filters.register(FilterCustomFishType)
        Triggers.register(TriggerCatchFish)
        Triggers.register(TriggerCatchFishFail)
    }

    override fun getPluginName(): String {
        return "CustomFishing"
    }
}