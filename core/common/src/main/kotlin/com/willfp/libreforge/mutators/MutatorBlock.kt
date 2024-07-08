package com.willfp.libreforge.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

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

    fun transform(parameters: Set<TriggerParameter>) =
        mutator.transform(parameters)
}
