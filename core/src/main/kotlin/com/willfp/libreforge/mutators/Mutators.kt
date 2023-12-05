package com.willfp.libreforge.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.mutators.impl.*

object Mutators: Registry<Mutator<*>>() {
    /**
     * Compile a list of [configs] into a MutatorList in a given [context].
     */
    fun compile(configs: Collection<Config>, context: ViolationContext): MutatorList =
        MutatorList(configs.mapNotNull { compile(it, context) })

    /**
     * Compile a [config] into a MutatorBlock in a given [context].
     */
    fun compile(config: Config, context: ViolationContext): MutatorBlock<*>? {
        val mutatorID = config.getString("id")
        val mutator = get(mutatorID)

        if (mutator == null) {
            context.log(ConfigViolation("id", "Invalid mutator ID specified: ${mutatorID}!"))
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
        register(MutatorBlockToLocation)
        register(MutatorLocationToBlock)
        register(MutatorLocationToCursor)
        register(MutatorLocationToPlayer)
        register(MutatorLocationToProjectile)
        register(MutatorLocationToVictim)
        register(MutatorPlayerAsVictim)
        register(MutatorSpinLocation)
        register(MutatorSpinVelocity)
        register(MutatorTranslateLocation)
        register(MutatorVictimAsPlayer)
        register(MutatorVictimToOwner)
        register(MutatorVictimAsDispatcher)
        register(MutatorDispatcherAsPlayer)
        register(MutatorDispatcherAsVictim)
        register(MutatorLocationToDrop)
    }
}
