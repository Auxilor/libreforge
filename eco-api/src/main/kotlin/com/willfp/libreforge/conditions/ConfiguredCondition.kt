package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config

data class ConfiguredCondition(val condition: Condition, val config: Config)