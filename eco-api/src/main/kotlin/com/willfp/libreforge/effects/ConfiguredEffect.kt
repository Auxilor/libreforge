package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.Trigger

data class ConfiguredEffect(
    val effect: Effect,
    val args: JSONConfig,
    val filter: Filter,
    val triggers: Collection<Trigger>
)