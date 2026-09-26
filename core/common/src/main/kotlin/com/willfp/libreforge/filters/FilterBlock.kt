package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.DynamicConfigs
import com.willfp.libreforge.triggers.TriggerData

/**
 * A single filter config block.
 */
class FilterBlock<T, V> internal constructor(
    val filter: Filter<T, V>,
    override val config: Config,
    override val compileData: T,
    val isInverted: Boolean
) : Compiled<T> {
    internal val key: String
        get() = if (isInverted) "not_${filter.id}" else filter.id

    private class Constant<V>(val value: V)

    // Read once if it cannot change between triggers: no placeholders and no random.
    private val constant: Constant<V>? by lazy(LazyThreadSafetyMode.PUBLICATION) {
        if (filter.isValueCacheable && !DynamicConfigs.isDynamicValue(config.get(key))) {
            Constant(filter.getValue(config, null, key))
        } else {
            null
        }
    }

    /**
     * The config value for a trigger, cached when it is constant.
     */
    internal fun valueFor(data: TriggerData): V {
        val cached = constant
        return if (cached != null) cached.value else filter.getValue(config, data, key)
    }

    fun isMet(data: TriggerData) =
        filter.isMet(data, this)
}
