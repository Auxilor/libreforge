package com.willfp.libreforge.counters.bind

import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.evaluateExpression
import com.willfp.libreforge.counters.Accumulator
import com.willfp.libreforge.counters.Counter
import com.willfp.libreforge.triggers.DispatchedTrigger

/*

The current binding system feels a bit messy, so it's all marked as internal since
I'll probably change it later, and I don't want someone to hook into a system
that may break between updates.

 */

internal data class BoundCounter(
    val counter: Counter,
    val accumulator: Accumulator
) {
    fun accept(trigger: DispatchedTrigger) {
        val data = trigger.data

        val player = trigger.player
        val value = data.value

        if (!counter.canBeTriggeredBy(trigger.trigger)) {
            return
        }

        if (!counter.conditions.areMet(player, data.holder)) {
            return
        }

        if (!counter.filters.isMet(data)) {
            return
        }

        val config = counter.config

        // Inject placeholders, totally not stolen from ElementLike
        listOf(counter.filters, counter.conditions)
            .flatten()
            .map { it.config }
            .plusElement(config)
            .forEach { it.addInjectablePlaceholder(trigger.placeholders) }

        val (argumentsMet, met, notMet) = counter.arguments.checkMet(counter, trigger)

        if (!argumentsMet) {
            notMet.forEach { it.ifNotMet(counter, trigger) }
            return
        }

        val multiplier = evaluateExpression(
            counter.multiplierExpression,
            placeholderContext(
                player = player,
                injectable = config
            )
        )

        met.forEach { it.ifMet(counter, trigger) }

        accumulator.accept(player, value * multiplier)
    }
}
