package com.willfp.libreforge.provider

import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.ConfiguredEffect

interface Holder {
    val effects: Set<ConfiguredEffect>
    val conditions: Set<ConfiguredCondition>
}