package com.willfp.libreforge.api.provider

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.conditions.Condition
import com.willfp.libreforge.api.effects.Effect

interface Holder {
    val effects: Map<Effect, JSONConfig>
    val conditions: Map<Condition, JSONConfig>
}