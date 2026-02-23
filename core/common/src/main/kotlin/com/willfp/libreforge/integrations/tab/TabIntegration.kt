package com.willfp.libreforge.integrations.tab

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.tab.impl.ConditionHasBossBarVisible
import com.willfp.libreforge.integrations.tab.impl.ConditionHasScoreboardVisible

object TabIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionHasBossBarVisible)
        Conditions.register(ConditionHasScoreboardVisible)
    }

    override fun getPluginName(): String {
        return "TAB"
    }
}
