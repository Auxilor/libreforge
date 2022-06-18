package com.willfp.libreforge.integrations.ecoarmor

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Condition

object EcoArmorIntegration: Integration {
    private lateinit var IS_WEARING_SET: Condition

    fun load() {
        IS_WEARING_SET = ConditionIsWearingSet()
    }

    override fun getPluginName(): String {
        return "EcoArmor"
    }
}
