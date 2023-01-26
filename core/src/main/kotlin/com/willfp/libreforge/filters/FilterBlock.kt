package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfiguredProperty
import com.willfp.libreforge.triggers.TriggerData

/**
 * A single filter config block.
 */
class FilterBlock<T, C>(
    val filter: Filter<T, C>,
    override val config: Config,
    override val compileData: T?
) : ConfiguredProperty<T> {
    fun filter(data: TriggerData) =
        filter.filter(data, this)
}
