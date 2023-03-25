package com.willfp.libreforge.loader.internal.configs

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.configs.LibreforgeObjectConfig
import com.willfp.libreforge.loader.configs.ConfigCategory
import java.io.File

internal data class RegistrableConfig(
    val config: Config,
    val file: File?,
    val id: String,
    val category: ConfigCategory
) {
    val handle = LibreforgeObjectConfig(
        config,
        file?.readText() ?: config.toPlaintext(),
        id,
        category.handle
    )
}
