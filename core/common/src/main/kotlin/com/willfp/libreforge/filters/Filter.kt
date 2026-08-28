package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Compilable
import com.willfp.libreforge.InvalidCompileDataException
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.triggers.TriggerData
import kotlin.reflect.KClass

abstract class Filter<T, V>(
    override val id: String
) : Compilable<T>() {
    /**
     * The run order.
     */
    open val runOrder = RunOrder.NORMAL

    /**
     * The type of value this filter reads from config, used for wiki documentation
     * and interactive config builders. Filters do not use arguments blocks —
     * the filter ID is the config key and this field declares what value type it expects.
     */
    open val valueType: ArgType = ArgType.ANY

    /**
     * For filters whose value is the constant(s) of an enum (usually a Bukkit enum such as
     * org.bukkit.event.entity.EntityDamageEvent.DamageCause): the enum the value belongs to.
     * Documentation-only — the wiki links to the enum's JavaDoc rather than listing constants,
     * which drift between Minecraft versions. See ArgumentMeta.Regular.enumClass, the
     * equivalent for arguments blocks.
     */
    open val valueEnumClass: KClass<out Enum<*>>? = null

    /**
     * Fetch value from config.
     *
     * [data] is null when generating compile data.
     */
    abstract fun getValue(config: Config, data: TriggerData?, key: String): V

    /**
     * Filter the trigger data.
     *
     * @param data The data.
     * @param config The config.
     * @return The modified data.
     */
    fun isMet(
        data: TriggerData,
        config: FilterBlock<T, V>
    ): Boolean {
        val cfg = config.config

        return if (config.isInverted) {
            !isMet(data, getValue(cfg, data, "not_$id"), config.compileData)
        } else {
            isMet(data, getValue(cfg, data, id), config.compileData)
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
    protected abstract fun isMet(
        data: TriggerData,
        value: V,
        compileData: T
    ): Boolean

    /*

    This really isn't the best way of doing this, but it's probably better
    than duplicating the compilable code, and it helps with compatibility.

    It's not like makeCompileData is ever used in a generic context anyway?
    It's there more as a template, like the rest of Compilable.

     */

    final override fun makeCompileData(config: Config, context: ViolationContext): T {
        throw UnsupportedOperationException("Use makeCompileData(Config, ViolationContext, V) instead!")
    }

    /**
     * @param config The config.
     * @param context The context to log violations for.
     * @return The compile data.
     */
    open fun makeCompileData(config: Config, context: ViolationContext, values: V): T {
        @Suppress("UNCHECKED_CAST")
        return NoCompileData as? T
            ?: throw InvalidCompileDataException(
                "You must override makeCompileData or use NoCompileData as the type!"
            )
    }
}
