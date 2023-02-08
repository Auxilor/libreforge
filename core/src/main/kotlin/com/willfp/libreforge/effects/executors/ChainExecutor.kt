package com.willfp.libreforge.effects.executors

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.triggers.DispatchedTrigger

/**
 * Executes a chain in a certain way.
 */
interface ChainExecutor {
    fun execute(
        chain: Chain,
        trigger: DispatchedTrigger
    ): Boolean
}

/**
 * Creates chain executors.
 */
abstract class ChainExecutorFactory(
    val id: String
) {
    abstract fun create(): ChainExecutor
}
