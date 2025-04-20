package com.willfp.libreforge.loader.configs

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.configs.LibreforgeConfigCategory
import com.willfp.libreforge.loader.LibreforgePlugin

/**
 * A category of configs, for example enchants, or sets, or skills.
 */
abstract class ConfigCategory(
    val id: String,
    val directory: String
) {
    open val legacyLocation: LegacyLocation? = null

    open val supportsSharing = true

    open val shouldPreload = false

    internal lateinit var handle: LibreforgeConfigCategory
        private set

    internal fun makeHandle(plugin: EcoPlugin) {
        handle = LibreforgeConfigCategory(id, directory, supportsSharing, plugin)
    }

    /**
     * Clear all configs.
     */
    abstract fun clear(plugin: LibreforgePlugin)

    /**
     * Accept a config found in the directory (e.g. load it).
     */
    abstract fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config)

    /**
     * Run before reloading.
     */
    open fun beforeReload(plugin: LibreforgePlugin) {
        // Override when needed.
    }

    /**
     * Run after reloading.
     */
    open fun afterReload(plugin: LibreforgePlugin) {
        // Override when needed.
    }

    /**
     * Accept a config in preloading.
     */
    open fun acceptPreloadConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        acceptConfig(plugin, id, config)
    }
}
