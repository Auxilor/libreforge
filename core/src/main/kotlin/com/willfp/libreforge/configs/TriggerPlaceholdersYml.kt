package com.willfp.libreforge.configs

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType

class TriggerPlaceholdersYml (
    plugin: EcoPlugin
): BaseConfig(
    "trigger-placeholders",
    plugin,
    true,
    ConfigType.YAML
)