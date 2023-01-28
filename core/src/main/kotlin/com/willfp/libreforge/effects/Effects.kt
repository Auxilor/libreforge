package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.argument.EffectArguments
import com.willfp.libreforge.effects.triggerer.ChainTriggerer
import com.willfp.libreforge.effects.triggerer.ChainTriggerers
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.mutators.Mutators
import com.willfp.libreforge.triggers.Triggers
import java.util.UUID

object Effects {
    private val registry = mutableMapOf<String, Effect<*>>()

    /**
     * Get an effect by [id].
     */
    fun getByID(id: String): Effect<*>? {
        return registry[id]
    }

    /**
     * Register a new [effect].
     */
    fun register(effect: Effect<*>) {
        registry[effect.id] = effect
    }

    /**
     * Compile a list of [configs] into an EffectList in a given [context].
     */
    fun compile(configs: Collection<Config>, context: ViolationContext): EffectList =
        EffectList(configs.mapNotNull { compile(it, context) })

    /**
     * Compile a [config] into an EffectBlock in a given [context].
     */
    fun compile(config: Config, context: ViolationContext): EffectBlock? {
        val args = config.getSubsection("args")

        val arguments = EffectArguments.compile(args, context.with("args"))
        val conditions = Conditions.compile(config.getSubsections("conditions"), context.with("conditions"))
        val mutators = Mutators.compile(config.getSubsections("mutators"), context.with("mutators"))
        val filters = Filters.compile(config.getSubsection("filters"), context.with("filters"))
        val triggers = config.getStrings("triggers").mapNotNull {
            Triggers.getByID(it)
        }

        val effectConfigs = if (config.has("id")) {
            listOf(config)
        } else {
            config.getSubsections("effects")
        }

        val directIDSpecified = config.has("id")

        val triggerer = ChainTriggerers.getByID(args.getString("run-type"))

        if (triggerer == null) {
            context.with("args")
                .log(ConfigViolation("run-type", "Invalid run type specified!"))
            return null
        }

        val chain = compileChain(effectConfigs, triggerer, context, directIDSpecified) ?: return null

        if (triggers.isNotEmpty() && chain.any { it.effect.isPermanent }) {
            context.log(ConfigViolation("triggers", "Triggers are not allowed on permanent effects!"))
            return null
        }

        if (triggers.isEmpty() && chain.any { !it.effect.isPermanent }) {
            context.log(ConfigViolation("triggers", "You must specify at least one trigger!"))
            return null
        }

        var isInvalid = false
        for (element in chain) {
            for (trigger in triggers) {
                if (!element.effect.supportsTrigger(trigger)) {
                    isInvalid = true
                    context.log(
                        ConfigViolation(
                            "triggers",
                            "${element.effect.id} does not support trigger ${trigger.id}"
                        )
                    )
                }
            }

            if (!triggers.all { element.effect.supportsTrigger(it) }) {
                isInvalid = true
            }
        }

        if (isInvalid) {
            return null
        }

        return EffectBlock(
            UUID.randomUUID(),
            args,
            chain,
            triggers,
            arguments,
            conditions,
            mutators,
            filters
        )
    }

    /**
     * Compile a list of [configs] and a [triggerer] into a Chain in a given [context].
     */
    private fun compileChain(
        configs: Collection<Config>,
        triggerer: ChainTriggerer,
        context: ViolationContext,
        directIDSpecified: Boolean // If it's configured with 'id', rather than 'effects'
    ): Chain? {
        val elements = configs.mapNotNull { compileElement(it, context) }

        if ((elements.size > 1 || !directIDSpecified) && elements.any { it.effect.isPermanent }) {
            context.log(ConfigViolation("effects", "Permanent effects are not allowed in chains!"))
            return null
        }

        return Chain(elements, triggerer)
    }

    private fun compileElement(config: Config, context: ViolationContext): ChainElement<*>? {
        val effect = getByID(config.getString("id"))

        if (effect == null) {
            context.log(ConfigViolation("id", "Invalid effect ID specified!"))
            return null
        }

        return makeElement(effect, config, context)
    }

    private fun <T> makeElement(
        effect: Effect<T>,
        config: Config,
        context: ViolationContext
    ): ChainElement<T>? {
        val args = config.getSubsection("args")

        if (!effect.checkConfig(args, context.with("args"))) {
            return null
        }

        val compileData = effect.makeCompileData(args, context.with("args"))

        val arguments = EffectArguments.compile(args, context.with("args"))
        val conditions = Conditions.compile(config.getSubsections("conditions"), context.with("conditions"))
        val mutators = Mutators.compile(config.getSubsections("mutators"), context.with("mutators"))
        val filters = Filters.compile(config.getSubsection("filters"), context.with("filters"))

        return ChainElement(
            effect,
            args,
            compileData,
            arguments,
            conditions,
            mutators,
            filters
        )
    }

    init {

    }
}
