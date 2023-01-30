package com.willfp.libreforge.effects.triggerers

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.triggers.DispatchedTrigger

/**
 * Triggers a chain in a certain way.
 */
interface ChainTriggerer {
    fun trigger(
        chain: Chain,
        trigger: DispatchedTrigger
    )
}

/**
 * Creates chain triggerers.
 */
abstract class ChainTriggererFactory(
    val id: String
) {
    abstract fun create(): ChainTriggerer
}
