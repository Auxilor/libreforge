package com.willfp.libreforge.integrations.ecoitems

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Condition

object EcoItemsIntegration: Integration {
    private lateinit var HAS_ECOITEM: Condition

    fun load() {
        HAS_ECOITEM = ConditionHasEcoItem()
    }

    override fun getPluginName(): String {
        return "EcoItems"
    }
}
