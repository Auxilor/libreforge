package com.willfp.libreforge.integrations.levelledmobs

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.LibReforgePlugin

object LevelledMobsIntegration : Integration {
    fun load(plugin: LibReforgePlugin) {
        plugin.eventManager.registerListener(LevelledMobsPlaceholderListener)
    }

    override fun getPluginName(): String {
        return "LevelledMobs"
    }
}