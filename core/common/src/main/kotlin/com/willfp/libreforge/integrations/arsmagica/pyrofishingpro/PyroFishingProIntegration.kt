package com.willfp.libreforge.integrations.arsmagica.pyrofishingpro

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.arsmagica.pyrofishingpro.impl.FilterFishHotspot
import com.willfp.libreforge.integrations.arsmagica.pyrofishingpro.impl.FilterFishTier
import com.willfp.libreforge.integrations.arsmagica.pyrofishingpro.impl.TriggerCatchFish
import com.willfp.libreforge.triggers.Triggers

object PyroFishingProIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Filters.register(FilterFishTier)
        Filters.register(FilterFishHotspot)
        Triggers.register(TriggerCatchFish)
    }

    override fun getPluginName(): String {
        return "PyroFishingPro"
    }
}