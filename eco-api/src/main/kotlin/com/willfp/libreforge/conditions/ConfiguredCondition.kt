package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.JSONConfig

data class ConfiguredCondition(val condition: Condition, val config: JSONConfig)