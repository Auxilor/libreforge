package com.willfp.libreforge.configs

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType

class SeasonsYml(
    plugin: EcoPlugin
): BaseConfig(
    "seasons",
    plugin,
    true,
    ConfigType.YAML
)
