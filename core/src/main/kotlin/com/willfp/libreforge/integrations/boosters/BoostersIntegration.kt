package com.willfp.libreforge.integrations.boosters

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.boosters.impl.ConditionIsBoosterActive

object BoostersIntegration : Integration {
    fun load() {
        Conditions.register(ConditionIsBoosterActive)
    }

    override fun getPluginName(): String {
        return "Boosters"
    }
}
