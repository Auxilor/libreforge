package com.willfp.libreforge.integrations.axplugins.axenvoy

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.axplugins.axenvoy.impl.FilterEnvoyType
import com.willfp.libreforge.integrations.axplugins.axenvoy.impl.TriggerCollectEnvoy
import com.willfp.libreforge.triggers.Triggers

object AxEnvoyIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerCollectEnvoy)
        Filters.register(FilterEnvoyType)
    }

    override fun getPluginName(): String {
        return "AxEnvoy"
    }
}
