package com.willfp.libreforge.configs

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType

class PlaceholdersYml(
    plugin: EcoPlugin
): BaseConfig(
    "placeholders",
    plugin,
    true,
    ConfigType.YAML
)

