package com.willfp.libreforge.loader.configs

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.configs.LibreforgeConfigCategory

abstract class ConfigCategory(
    private val id: String,
    val directory: String
) {
    internal lateinit var handle: LibreforgeConfigCategory
        private set

    internal fun makeHandle(plugin: EcoPlugin) {
        handle = LibreforgeConfigCategory(id, directory, plugin)
    }

    /**
     * Accept a config found in the directory (e.g. load it).
     */
    abstract fun acceptConfig(config: Config)

    /**
     * Run before reloading.
     */
    open fun beforeReload() {
        // Override when needed.
    }

    /**
     * Run after reloading.
     */
    open fun afterReload() {
        // Override when needed.
    }
}
