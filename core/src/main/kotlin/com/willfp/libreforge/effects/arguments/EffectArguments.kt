package com.willfp.libreforge.effects.arguments

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.arguments.impl.ArgumentChainArgs
import com.willfp.libreforge.effects.arguments.impl.ArgumentChance
import com.willfp.libreforge.effects.arguments.impl.ArgumentCooldown
import com.willfp.libreforge.effects.arguments.impl.ArgumentCost
import com.willfp.libreforge.effects.arguments.impl.ArgumentEvery
import com.willfp.libreforge.effects.arguments.impl.ArgumentPointCost
import com.willfp.libreforge.effects.arguments.impl.ArgumentPrice
import com.willfp.libreforge.effects.arguments.impl.ArgumentRequire

object EffectArguments {
    private val registry = mutableMapOf<String, EffectArgument<*>>()

    /**
     * Get an effect argument by [id].
     */
    fun getByID(id: String): EffectArgument<*>? {
        return registry[id]
    }

    /**
     * Register a new [argument].
     */
    fun register(argument: EffectArgument<*>) {
        registry[argument.id] = argument
    }

    /**
     * Compile a [config] into an EffectArgumentList a given [context].
     */
    fun compile(config: Config, context: ViolationContext): EffectArgumentList {
        val blocks = mutableListOf<EffectArgumentBlock<*>>()

        for (key in config.getKeys(false)) {
            val argument = getByID(key) ?: continue
            blocks += makeBlock(argument, config, context) ?: continue
        }

        return EffectArgumentList(blocks)
    }

    private fun <T> makeBlock(
        argument: EffectArgument<T>,
        config: Config,
        context: ViolationContext
    ): EffectArgumentBlock<T>? {
        if (!argument.checkConfig(config, context)) {
            return null
        }

        val compileData = argument.makeCompileData(config, context)
        return EffectArgumentBlock(argument, config, compileData)
    }

    init {
        register(ArgumentChainArgs)
        register(ArgumentChance)
        register(ArgumentCooldown)
        register(ArgumentCost)
        register(ArgumentEvery)
        register(ArgumentPointCost)
        register(ArgumentPrice)
        register(ArgumentRequire)
    }
}
