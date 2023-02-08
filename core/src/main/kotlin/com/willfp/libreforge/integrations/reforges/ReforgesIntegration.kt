package com.willfp.libreforge.integrations.reforges

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.reforges.impl.ConditionHasReforge

object ReforgesIntegration : Integration {
    fun load() {
        Conditions.register(ConditionHasReforge)
    }

    override fun getPluginName(): String {
        return "Reforges"
    }
}
