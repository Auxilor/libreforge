package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigurableProperty
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.triggers.TriggerData

abstract class Filter<T, C>(
    override val id: String
) : ConfigurableProperty() {
    /**
     * The run order.
     */
    open val runOrder = RunOrder.NORMAL

    /**
     * Fetch values from config.
     */
    abstract fun getValues(config: Config, key: String): C

    /**
     * Filter the trigger data.
     *
     * Return true if allowed, false if not.
     *
     * @param data The data.
     * @param config The config.
     * @return The modified data.
     */
    fun filter(
        data: TriggerData,
        config: FilterBlock<T, C>
    ): Boolean {
        val cfg = config.config

        val regularPresent = cfg.has(id)
        val inversePresent = cfg.has("not_$id")

        if (!regularPresent && !inversePresent) {
            return true
        }

        return if (inversePresent) {
            !filter(data, getValues(cfg, "not_$id"), config.compileData)
        } else {
            filter(data, getValues(cfg, id), config.compileData)
        }
    }

    /**
     * Filter the trigger data.
     *
     * Return true if allowed, false if not.
     *
     * @param data The data.
     * @param values The values from config.
     * @param compileData The compile data.
     * @return The modified data.
     */
    protected abstract fun filter(
        data: TriggerData,
        values: C,
        compileData: T?
    ): Boolean

    /**
     * @param config The config.
     * @param context The context to log violations for.
     * @return The compile data.
     */
    open fun makeCompileData(config: Config, values: C, context: ViolationContext): T? {
        return null
    }
}
