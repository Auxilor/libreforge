package com.willfp.libreforge.integrations.fancynpcs

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.fancynpcs.impl.FilterNPC
import com.willfp.libreforge.integrations.fancynpcs.impl.TriggerLeftClickNPC
import com.willfp.libreforge.integrations.fancynpcs.impl.TriggerRightClickNPC
import com.willfp.libreforge.triggers.Triggers

object FancyNPCsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Filters.register(FilterNPC)
        Triggers.register(TriggerLeftClickNPC)
        Triggers.register(TriggerRightClickNPC)
    }

    override fun getPluginName(): String {
        return "FancyNpcs"
    }
}