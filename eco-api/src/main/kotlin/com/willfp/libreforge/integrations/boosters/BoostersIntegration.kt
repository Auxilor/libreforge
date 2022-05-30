package com.willfp.libreforge.integrations.boosters

import com.willfp.libreforge.conditions.Condition

object BoostersIntegration {
    private lateinit var IS_BOOSTER_ACTIVE: Condition

    fun load() {
        IS_BOOSTER_ACTIVE = ConditionIsBoosterActive()
    }
}
