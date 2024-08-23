package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.triggers.TriggerData

/**
 * A single filter config block.
 */
class FilterBlock<T, V> internal constructor(
    val filter: Filter<T, V>,
    override val config: Config,
    override val compileData: T
) : Compiled<T> {
    val isInverted: Boolean? by lazy {
        val cfg = config

        val regularPresent = cfg.has(filter.id)
        val inversePresent = cfg.has("not_${filter.id}")

        if (!regularPresent && !inversePresent) {
            null
        } else {
            inversePresent
        }
    }

    fun isMet(data: TriggerData) =
        filter.isMet(data, this)
}
