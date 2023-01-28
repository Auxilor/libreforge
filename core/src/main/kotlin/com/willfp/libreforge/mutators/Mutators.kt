package com.willfp.libreforge.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext

object Mutators {
    private val registry = mutableMapOf<String, Mutator<*>>()

    /**
     * Get a mutator by [id].
     */
    fun getByID(id: String): Mutator<*>? {
        return registry[id]
    }

    /**
     * Register a new [mutator].
     */
    fun register(mutator: Mutator<*>) {
        registry[mutator.id] = mutator
    }

    /**
     * Compile a list of [configs] into a MutatorList in a given [context].
     */
    fun compile(configs: Collection<Config>, context: ViolationContext): MutatorList =
        MutatorList(configs.mapNotNull { compile(it, context) })

    /**
     * Compile a [config] into a MutatorBlock in a given [context].
     */
    fun compile(config: Config, context: ViolationContext): MutatorBlock<*>? {
        val mutator = getByID(config.getString("id"))

        if (mutator == null) {
            context.log(ConfigViolation("id", "Invalid mutator ID specified!"))
            return null
        }

        return makeBlock(mutator, config.getSubsection("args"), context.with("args"))
    }

    private fun <T> makeBlock(
        mutator: Mutator<T>,
        config: Config,
        context: ViolationContext
    ): MutatorBlock<T>? {
        if (!mutator.checkConfig(config, context)) {
            return null
        }

        val compileData = mutator.makeCompileData(config, context)

        return MutatorBlock(
            mutator,
            config,
            compileData,
        )
    }

    init {

    }
}
