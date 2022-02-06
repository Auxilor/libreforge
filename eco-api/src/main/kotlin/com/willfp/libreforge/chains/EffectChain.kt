package com.willfp.libreforge.chains

import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.triggers.InvocationData

class EffectChain(
    val id: String,
    val effects: Iterable<ConfiguredEffect>
) {
    operator fun invoke(invocationData: InvocationData) {
        for (effect in effects) {
            effect(invocationData, ignoreTriggerList = true)
        }
    }
}
