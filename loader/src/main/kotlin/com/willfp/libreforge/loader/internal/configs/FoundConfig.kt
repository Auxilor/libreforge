package com.willfp.libreforge.loader.internal.configs

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.ExtendableConfig
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory

internal class FoundConfig(
    name: String,
    category: ConfigCategory,
    plugin: LibreforgePlugin
) : ExtendableConfig(
    name,
    true,
    plugin,
    plugin::class.java,
    "${category.directory}/",
    ConfigType.YAML
)
