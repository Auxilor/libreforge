package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compilable
import com.willfp.libreforge.InvalidCompileDataException
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.triggers.TriggerData

abstract class Filter<T, C>(
    override val id: String
) : Compilable<T>() {
    /**
     * The run order.
     */
    open val runOrder = RunOrder.NORMAL

    /**
     * Fetch value from config.
     *
     * [data] is null when generating compile data.
     */
    abstract fun getValue(config: Config, data: TriggerData?, key: String): C

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
            !filter(data, getValue(cfg, data, "not_$id"), config.compileData)
        } else {
            filter(data, getValue(cfg, data, id), config.compileData)
        }
    }

    /**
     * Filter the trigger data.
     *
     * Return true if allowed, false if not.
     *
     * @param data The data.
     * @param value The value from config.
     * @param compileData The compile data.
     * @return The modified data.
     */
    protected abstract fun filter(
        data: TriggerData,
        value: C,
        compileData: T
    ): Boolean

    /*

    This really isn't the best way of doing this, but it's probably better
    than duplicating the compilable code, and it helps with compatibility.

    It's not like makeCompileData is ever used in a generic context anyway?
    It's there more as a template, like the rest of Compilable.

     */

    final override fun makeCompileData(config: Config, context: ViolationContext): T {
        throw UnsupportedOperationException("Use makeCompileData(Config, ViolationContext, C) instead!")
    }

    /**
     * @param config The config.
     * @param context The context to log violations for.
     * @return The compile data.
     */
    open fun makeCompileData(config: Config, context: ViolationContext, values: C): T {
        @Suppress("UNCHECKED_CAST")
        return NoCompileData as? T
            ?: throw InvalidCompileDataException(
                "You must override makeCompileData or use NoCompileData as the type!"
            )
    }
}
