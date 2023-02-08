package com.willfp.libreforge.lrcdb

import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType
import com.willfp.libreforge.LibReforgePlugin

class LrcdbYml(
    plugin: LibReforgePlugin
) : BaseConfig(
    "lrcdb",
    plugin,
    true,
    ConfigType.YAML
)
