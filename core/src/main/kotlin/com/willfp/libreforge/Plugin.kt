package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin

internal val plugin: EcoPlugin
    get() = LibreforgeInitializer.plugin

object LibreforgeInitializer {
    internal lateinit var plugin: EcoPlugin

    fun addPlugin(plugin: EcoPlugin) {
        if (!this::plugin.isInitialized) {
            this.plugin = plugin
        }
    }
}
