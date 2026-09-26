package com.willfp.libreforge.counters.bind

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.evaluateExpression
import com.willfp.libreforge.DynamicConfigs
import com.willfp.libreforge.counters.Accumulator
import com.willfp.libreforge.counters.Counter
import com.willfp.libreforge.get
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.entity.Player

/*

The current binding system feels a bit messy, so it's all marked as internal since
I'll probably change it later, and I don't want someone to hook into a system
that may break between updates.

 */

internal data class BoundCounter(
    val counter: Counter,
    val accumulator: Accumulator
) {
    // Only configs that reference a placeholder need trigger placeholders injected.
    private val filterConfigsToInject: List<Config> by lazy {
        counter.filters.map { it.config }.distinct().filter { DynamicConfigs.hasPlaceholder(it) }
    }

    private val conditionConfigsToInject: List<Config> by lazy {
        counter.conditions.map { it.config }.filter { DynamicConfigs.hasPlaceholder(it) }
    }

    private val injectIntoConfig: Boolean by lazy {
        DynamicConfigs.hasPlaceholder(counter.config)
                || counter.valueExpression?.contains('%') == true
                || counter.multiplierExpression?.contains('%') == true
    }

    /**
     * Called only for dispatches of the counter's own trigger (bindings are indexed by trigger).
     */
    fun accept(trigger: DispatchedTrigger) {
        val data = trigger.data

        val player = trigger.dispatcher.get<Player>() ?: return
        val value = data.value

        // Inject placeholders before anything reads them, totally not stolen from ElementLike
        for (config in filterConfigsToInject) {
            config.addInjectablePlaceholder(trigger.placeholders)
        }

        if (!counter.filters.isMet(data)) {
            return
        }

        for (config in conditionConfigsToInject) {
            config.addInjectablePlaceholder(trigger.placeholders)
        }

        if (!counter.conditions.areMetAndTrigger(trigger)) {
            return
        }

        val config = counter.config

        if (injectIntoConfig) {
            config.addInjectablePlaceholder(trigger.placeholders)
        }

        val (argumentsMet, met, notMet) = counter.arguments.checkMet(counter, trigger)

        if (!argumentsMet) {
            notMet.forEach { it.ifNotMet(counter, trigger) }
            return
        }

        met.forEach { it.ifMet(counter, trigger) }

        if (counter.valueExpression != null) {
            val valueFromExpr = evaluateExpression(
                counter.valueExpression,
                placeholderContext(
                    player = player,
                    injectable = config
                )
            )

            accumulator.accept(player, valueFromExpr)
            return
        }

        if (counter.multiplierExpression != null) {
            val multiplier = evaluateExpression(
                counter.multiplierExpression,
                placeholderContext(
                    player = player,
                    injectable = config
                )
            )

            accumulator.accept(player, value * multiplier)
            return
        }

        accumulator.accept(player, value)
    }
}
