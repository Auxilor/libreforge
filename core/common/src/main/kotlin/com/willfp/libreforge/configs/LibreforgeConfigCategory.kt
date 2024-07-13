package com.willfp.libreforge.configs

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.core.registry.Registry

data class LibreforgeConfigCategory(
    override val id: String,
    val directory: String,
    val supportsSharing: Boolean,
    val plugin: EcoPlugin
): Registry<LibreforgeObjectConfig>(), KRegistrable
