package com.willfp.libreforge.integrations.ecoarmor

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.ecoarmor.impl.ConditionIsWearingSet

object EcoArmorIntegration: Integration {
    fun load() {
        Conditions.register(ConditionIsWearingSet)
    }

    override fun getPluginName(): String {
        return "EcoArmor"
    }
}
