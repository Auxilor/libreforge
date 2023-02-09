package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.triggers.TriggerData

/**
 * A single filter config block.
 */
class FilterBlock<T, C>(
    val filter: Filter<T, C>,
    override val config: Config,
    override val compileData: T
) : Compiled<T> {
    fun isMet(data: TriggerData) =
        filter.isMet(data, this)
}
