package com.willfp.libreforge.api.provider

import com.willfp.libreforge.api.conditions.ConfiguredCondition
import com.willfp.libreforge.api.effects.ConfiguredEffect

interface Holder {
    val effects: Set<ConfiguredEffect>
    val conditions: Set<ConfiguredCondition>
}