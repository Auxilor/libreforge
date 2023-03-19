package com.willfp.libreforge.integrations.ecoarmor

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.ecoarmor.impl.ConditionIsWearingSet

object EcoArmorIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionIsWearingSet)
    }

    override fun getPluginName(): String {
        return "EcoArmor"
    }
}
