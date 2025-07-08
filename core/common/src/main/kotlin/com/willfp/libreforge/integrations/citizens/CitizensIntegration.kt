package com.willfp.libreforge.integrations.citizens

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.citizens.impl.FilterNPC
import com.willfp.libreforge.integrations.citizens.impl.TriggerLeftClickNPC
import com.willfp.libreforge.integrations.citizens.impl.TriggerRightClickNPC
import com.willfp.libreforge.triggers.Triggers

object CitizensIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Filters.register(FilterNPC)
        Triggers.register(TriggerLeftClickNPC)
        Triggers.register(TriggerRightClickNPC)
    }

    override fun getPluginName(): String {
        return "Citizens"
    }
}
