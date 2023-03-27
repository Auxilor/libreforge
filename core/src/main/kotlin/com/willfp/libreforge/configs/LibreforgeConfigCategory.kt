package com.willfp.libreforge.configs

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.registry.Registrable
import com.willfp.eco.core.registry.Registry

data class LibreforgeConfigCategory(
    val id: String,
    val directory: String,
    val supportsSharing: Boolean,
    val plugin: EcoPlugin
): Registry<LibreforgeObjectConfig>(), Registrable {
    override fun getID(): String {
        return id
    }
}
