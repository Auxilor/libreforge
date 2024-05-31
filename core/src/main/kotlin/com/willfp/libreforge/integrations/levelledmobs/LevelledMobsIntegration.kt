package com.willfp.libreforge.integrations.levelledmobs

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.levelledmobs.impl.LevelledMobsPlaceholderListener

object LevelledMobsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        LevelledMobsPlaceholderListener.load()
        plugin.eventManager.registerListener(LevelledMobsPlaceholderListener)
    }

    override fun getPluginName(): String {
        return "LevelledMobs"
    }
}
