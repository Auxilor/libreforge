package com.willfp.libreforge.loader.configs

/**
 * Legacy location of a config.
 */
data class LegacyLocation(
    val filename: String,
    val section: String,
    val alternativeDirectories: List<String> = emptyList()
)
