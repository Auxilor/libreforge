package com.willfp.libreforge.integrations.boosters

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.boosters.impl.ConditionIsBoosterActive

object BoostersIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionIsBoosterActive)
    }

    override fun getPluginName(): String {
        return "Boosters"
    }
}
