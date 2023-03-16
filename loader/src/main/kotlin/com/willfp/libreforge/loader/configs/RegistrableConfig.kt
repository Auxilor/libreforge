package com.willfp.libreforge.loader.configs

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.configs.LibreforgeObjectConfig
import java.io.File

data class RegistrableConfig(
    val config: Config,
    val file: File?,
    val id: String,
    val category: ConfigCategory
) {
    internal val handle = LibreforgeObjectConfig(
        config,
        file?.readText() ?: config.toPlaintext(),
        id,
        category.handle
    )
}
