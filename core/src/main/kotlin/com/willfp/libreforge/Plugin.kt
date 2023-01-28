package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin

internal val plugin: EcoPlugin
    get() = LibreforgeInitializer.plugin ?: throw NoLibreforgePluginPresentError(
        "No libreforge plugin found on the server! Debug: ${LibreforgeInitializer.plugins}"
    )

private class NoLibreforgePluginPresentError(
    override val message: String
) : Error(message)

object LibreforgeInitializer {
    internal var plugin: EcoPlugin? = null

    internal val plugins = mutableListOf<EcoPlugin>()

    fun addPlugin(plugin: EcoPlugin) {
        if (this.plugin == null) {
            this.plugin = plugin
        }

        plugins += plugin

        // To prevent problems, when a libreforge plugin is disabled,
        // the core plugin is automatically switched to the next available
        // one.
        plugin.onDisable {
            plugins -= plugin

            if (this.plugin == plugin) {
                this.plugin = plugins.firstOrNull()
            }
        }
    }
}
