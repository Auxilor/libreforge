package com.willfp.libreforge.integrations.worldguard

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.worldguard.impl.ConditionInRegion

object WorldGuardIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionInRegion)
    }

    override fun getPluginName(): String {
        return "WorldGuard"
    }
}
