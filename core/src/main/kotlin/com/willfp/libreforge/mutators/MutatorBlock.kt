package com.willfp.libreforge.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.triggers.TriggerData

/**
 * A single mutator config block.
 */
class MutatorBlock<T>(
    val mutator: Mutator<T>,
    val config: Config,
    val compileData: T?
) {
    fun mutate(data: TriggerData) =
        mutator.mutate(data, this)
}
