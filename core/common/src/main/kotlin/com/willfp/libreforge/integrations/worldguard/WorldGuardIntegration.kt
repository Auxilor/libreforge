package com.willfp.libreforge.integrations.worldguard

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.worldguard.impl.ConditionInRegion
import com.willfp.libreforge.integrations.worldguard.impl.FilterRegion
import com.willfp.libreforge.integrations.worldguard.impl.TriggerEnterRegion
import com.willfp.libreforge.integrations.worldguard.impl.TriggerLeaveRegion
import com.willfp.libreforge.triggers.Triggers

object WorldGuardIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionInRegion)
        Filters.register(FilterRegion)
        Triggers.register(TriggerEnterRegion)
        Triggers.register(TriggerLeaveRegion)
    }

    override fun getPluginName(): String {
        return "WorldGuard"
    }
}
