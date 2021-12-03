package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.Trigger

data class ConfiguredEffect(
    val effect: Effect,
    val args: Config,
    val filter: Filter,
    val triggers: Collection<Trigger>
)