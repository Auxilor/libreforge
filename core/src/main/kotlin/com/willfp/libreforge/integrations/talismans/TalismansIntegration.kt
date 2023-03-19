package com.willfp.libreforge.integrations.talismans

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.talismans.impl.ConditionHasTalisman

object TalismansIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionHasTalisman)
    }

    override fun getPluginName(): String {
        return "Talismans"
    }
}
