package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Effects

object Conditions {
    private val registry = mutableMapOf<String, Condition<*>>()

    /**
     * Get a condition by [id].
     */
    fun getByID(id: String): Condition<*>? {
        return registry[id]
    }

    /**
     * Register a new [condition].
     */
    fun register(condition: Condition<*>) {
        registry[condition.id] = condition
    }

    /**
     * Compile a list of [configs] into a ConditionList in a given [context].
     */
    fun compile(configs: Collection<Config>, context: ViolationContext): ConditionList =
        ConditionList(configs.mapNotNull { compile(it, context) })

    /**
     * Compile a [config] into a ConditionBlock in a given [context].
     */
    fun compile(config: Config, context: ViolationContext): ConditionBlock<*>? {
        val condition = getByID(config.getString("id"))

        if (condition == null) {
            context.log(ConfigViolation("id", "Invalid condition ID specified!"))
            return null
        }

        return makeBlock(condition, config.getSubsection("args"), context.with("args"))
    }

    private fun <T> makeBlock(
        condition: Condition<T>,
        config: Config,
        context: ViolationContext
    ): ConditionBlock<T>? {
        if (!condition.checkConfig(config, context)) {
            return null
        }

        val compileData = condition.makeCompileData(config, context)
        val notMetEffects = Effects.compile(
            config.getSubsections("not-met-effects"),
            context.with("not-met-effects")
        )

        return ConditionBlock(
            condition,
            config,
            compileData,
            notMetEffects,
            config.getStrings("not-met-lines")
        )
    }

    init {

    }
}
