package com.willfp.libreforge.integrations.reforges

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.reforges.impl.ConditionHasReforge

object ReforgesIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionHasReforge)
    }

    override fun getPluginName(): String {
        return "Reforges"
    }
}
