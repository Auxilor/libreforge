package com.willfp.libreforge.integrations.boosters

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Condition

object BoostersIntegration : Integration {
    private lateinit var IS_BOOSTER_ACTIVE: Condition

    fun load() {
        IS_BOOSTER_ACTIVE = ConditionIsBoosterActive()
    }

    override fun getPluginName(): String {
        return "Boosters"
    }
}
