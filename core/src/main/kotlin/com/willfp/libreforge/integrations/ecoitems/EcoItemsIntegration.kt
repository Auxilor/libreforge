package com.willfp.libreforge.integrations.ecoitems

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.ecoitems.impl.ConditionHasEcoItem

object EcoItemsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionHasEcoItem)
    }

    override fun getPluginName(): String {
        return "EcoItems"
    }
}
