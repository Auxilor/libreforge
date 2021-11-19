package com.willfp.libreforge.api.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.filter.Filter
import com.willfp.libreforge.api.triggers.Trigger
import com.willfp.libreforge.internal.filter.CompoundFilter

data class ConfiguredEffect(
    val effect: Effect,
    val args: JSONConfig,
    val filter: Filter,
    val triggers: Collection<Trigger>
)