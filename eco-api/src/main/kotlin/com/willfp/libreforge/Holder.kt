package com.willfp.libreforge

import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.ConfiguredEffect

interface Holder {
    val id: String
    val effects: Set<ConfiguredEffect>
    val conditions: Set<ConfiguredCondition>
}
