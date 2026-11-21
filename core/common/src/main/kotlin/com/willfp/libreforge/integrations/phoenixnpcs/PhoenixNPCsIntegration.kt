package com.willfp.libreforge.integrations.phoenixnpcs

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.phoenixnpcs.impl.FilterPhoenixNPC
import com.willfp.libreforge.integrations.phoenixnpcs.impl.TriggerLeftClickPhoenixNPC
import com.willfp.libreforge.integrations.phoenixnpcs.impl.TriggerRightClickPhoenixNPC
import com.willfp.libreforge.triggers.Triggers

object PhoenixNPCsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerLeftClickPhoenixNPC)
        Triggers.register(TriggerRightClickPhoenixNPC)
        Filters.register(FilterPhoenixNPC)
    }

    override fun getPluginName() = "PhoenixNPCs"
}
