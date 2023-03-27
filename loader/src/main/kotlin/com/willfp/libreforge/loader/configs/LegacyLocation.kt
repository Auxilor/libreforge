package com.willfp.libreforge.loader.configs

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
)
