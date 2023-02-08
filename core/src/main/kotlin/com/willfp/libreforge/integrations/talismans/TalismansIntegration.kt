package com.willfp.libreforge.integrations.talismans

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.talismans.impl.ConditionHasTalisman

object TalismansIntegration: Integration {
    fun load() {
        Conditions.register(ConditionHasTalisman)
    }

    override fun getPluginName(): String {
        return "Talismans"
    }
}
