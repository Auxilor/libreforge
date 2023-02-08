package com.willfp.libreforge.integrations.ecoitems

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.ecoitems.impl.ConditionHasEcoItem

object EcoItemsIntegration: Integration {
    fun load() {
        Conditions.register(ConditionHasEcoItem)
    }

    override fun getPluginName(): String {
        return "EcoItems"
    }
}
