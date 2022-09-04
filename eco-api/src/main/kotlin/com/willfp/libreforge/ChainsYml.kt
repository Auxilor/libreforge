package com.willfp.libreforge

import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType

class ChainsYml(
    plugin: LibReforgePlugin
) : BaseConfig(
    "chains",
    plugin,
    true,
    ConfigType.YAML
)
