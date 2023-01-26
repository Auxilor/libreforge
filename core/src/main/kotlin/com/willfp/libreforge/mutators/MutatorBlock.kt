package com.willfp.libreforge.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfiguredProperty
import com.willfp.libreforge.triggers.TriggerData

/**
 * A single mutator config block.
 */
class MutatorBlock<T>(
    val mutator: Mutator<T>,
    override val config: Config,
    override val compileData: T?
): ConfiguredProperty<T> {
    fun mutate(data: TriggerData) =
        mutator.mutate(data, this)
}
