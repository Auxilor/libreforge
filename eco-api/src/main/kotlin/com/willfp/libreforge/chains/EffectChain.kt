package com.willfp.libreforge.chains

import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.triggers.InvocationData

class EffectChain(
    val id: String,
    private val components: Iterable<ChainComponent>
) {
    operator fun invoke(invocationData: InvocationData) {
        for (component in components) {
            when (component) {
                is ChainComponentEffect -> {
                    component.effect(invocationData, ignoreTriggerList = true)
                }
            }
        }
    }
}

sealed interface ChainComponent {

}

class ChainComponentEffect(
    val effect: ConfiguredEffect
) : ChainComponent
