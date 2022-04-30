package com.willfp.libreforge.integrations.ecoarmor

import com.willfp.libreforge.conditions.Condition

object EcoArmorIntegration {
    private lateinit var IS_WEARING_SET: Condition

    fun load() {
        IS_WEARING_SET = ConditionIsWearingSet()
    }
}
