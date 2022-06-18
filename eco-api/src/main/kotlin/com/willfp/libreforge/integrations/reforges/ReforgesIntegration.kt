package com.willfp.libreforge.integrations.reforges

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Condition

object ReforgesIntegration: Integration {
    private lateinit var HAS_REFORGE: Condition

    fun load() {
        HAS_REFORGE = ConditionHasReforge()
    }

    override fun getPluginName(): String {
        return "Reforges"
    }
}
