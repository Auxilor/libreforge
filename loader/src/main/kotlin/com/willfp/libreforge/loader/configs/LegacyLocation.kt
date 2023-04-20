package com.willfp.libreforge.loader.configs

import com.willfp.eco.core.config.emptyConfig
import com.willfp.eco.core.config.readConfig
import com.willfp.libreforge.loader.LibreforgePlugin

/**
 * Legacy location of a config.
 */
data class LegacyLocation(
    /**
     * The filename of the config, including the extension.
     */
    val filename: String,
    val section: String,
    val alternativeDirectories: List<String> = emptyList()
) {
    fun getConfig(plugin: LibreforgePlugin) =
        plugin.dataFolder.resolve(filename)
            .let { if (it.exists()) it.readConfig() else emptyConfig() }
}
