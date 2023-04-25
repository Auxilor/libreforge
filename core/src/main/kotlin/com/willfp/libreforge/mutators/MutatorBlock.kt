package com.willfp.libreforge.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.triggers.TriggerData

/**
 * A single mutator config block.
 */
class MutatorBlock<T> internal constructor(
    val mutator: Mutator<T>,
    override val config: Config,
    override val compileData: T
): Compiled<T> {
    fun mutate(data: TriggerData) =
        mutator.mutate(data, this)
}
