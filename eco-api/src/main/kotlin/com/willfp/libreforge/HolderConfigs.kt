package com.willfp.libreforge

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.ExtendableConfig

class HolderConfig(
    name: String,
    directory: String,
    plugin: LibReforgePlugin
) : ExtendableConfig(
    name,
    true,
    plugin,
    plugin::class.java,
    "$directory/",
    ConfigType.YAML
)
