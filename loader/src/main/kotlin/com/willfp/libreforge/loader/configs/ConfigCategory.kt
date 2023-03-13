package com.willfp.libreforge.loader.configs

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.configs.LibreforgeConfigCategory

abstract class ConfigCategory(
    id: String,
    val directory: String
) {
    internal val handle = LibreforgeConfigCategory(id, directory)

    /**
     * Accept a config found in the directory (e.g. load it).
     */
    abstract fun acceptConfig(config: Config)
}
