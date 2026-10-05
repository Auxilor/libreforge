package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.LifecyclePosition
import com.willfp.eco.core.PluginLike
import com.willfp.eco.core.registry.Registrable
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.configs.LibreforgeConfigCategory

object Plugins : Registry<LoadedLibreforgePlugin>()

/**
 * Loaded libreforge plugin, used to be able to access
 * categories from a specific plugin.
 */
interface LoadedLibreforgePlugin : Registrable, PluginLike {
    /**
     * The actual libreforge plugin instance.
     */
    val plugin: EcoPlugin

    /**
     * The config categories.
     */
    val categories: Registry<LibreforgeConfigCategory>

    override fun getID() = plugin.id

    override fun onRegister() {
        pointsPlaceholder(plugin).register()

        // After the plugin's categories have reloaded, so the re-ask sees the new holders.
        plugin.onReload(LifecyclePosition.END) {
            HolderStates.resetAllStates()
        }

        // While the plugin's code is still usable.
        plugin.onDisable(LifecyclePosition.START) {
            HolderStates.onPluginDisable(plugin)
        }
    }
}
