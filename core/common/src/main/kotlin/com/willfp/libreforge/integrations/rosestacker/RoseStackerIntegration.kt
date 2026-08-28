package com.willfp.libreforge.integrations.rosestacker

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.rosestacker.impl.RoseStackerBreedListener
import com.willfp.libreforge.integrations.rosestacker.impl.RoseStackerPlaceholderListener
import com.willfp.libreforge.integrations.rosestacker.impl.RoseStackerStackedDeathListener

object RoseStackerIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        plugin.eventManager.registerListener(RoseStackerBreedListener)
        plugin.eventManager.registerListener(RoseStackerStackedDeathListener)
        plugin.eventManager.registerListener(RoseStackerPlaceholderListener)
    }

    override fun getPluginName(): String {
        return "RoseStacker"
    }
}
