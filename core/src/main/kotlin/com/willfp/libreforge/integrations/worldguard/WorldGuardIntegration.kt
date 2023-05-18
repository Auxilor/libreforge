package com.willfp.libreforge.integrations.worldguard

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.worldguard.impl.ConditionInRegion
import com.willfp.libreforge.integrations.worldguard.impl.FilterRegions
import com.willfp.libreforge.integrations.worldguard.impl.TriggerEnterRegion
import com.willfp.libreforge.integrations.worldguard.impl.TriggerLeaveRegion
import com.willfp.libreforge.triggers.Triggers

object WorldGuardIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionInRegion)
        Triggers.register(TriggerEnterRegion)
        Triggers.register(TriggerLeaveRegion)
        Filters.register(FilterRegions)
    }

    override fun getPluginName(): String {
        return "WorldGuard"
    }
}
