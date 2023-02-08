package com.willfp.libreforge.integrations.levelledmobs

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.integrations.levelledmobs.impl.LevelledMobsPlaceholderListener
import com.willfp.libreforge.plugin

object LevelledMobsIntegration : Integration {
    fun load() {
        plugin.eventManager.registerListener(LevelledMobsPlaceholderListener)
    }

    override fun getPluginName(): String {
        return "LevelledMobs"
    }
}
