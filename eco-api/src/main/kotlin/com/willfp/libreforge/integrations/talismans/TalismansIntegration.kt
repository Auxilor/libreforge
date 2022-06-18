package com.willfp.libreforge.integrations.talismans

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Condition

object TalismansIntegration: Integration {
    private lateinit var HAS_TALISMAN: Condition

    fun load() {
        HAS_TALISMAN = ConditionHasTalisman()
    }

    override fun getPluginName(): String {
        return "Talismans"
    }
}
