package com.willfp.libreforge.api.effects

import com.willfp.eco.core.config.interfaces.JSONConfig

data class ConfiguredEffect(val effect: Effect, val config: JSONConfig)