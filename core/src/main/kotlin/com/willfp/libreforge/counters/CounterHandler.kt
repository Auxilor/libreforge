package com.willfp.libreforge.counters

import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

private val counters = listMap<Counter, Accumulator>()

internal fun bindCounter(counter: Counter, accumulator: Accumulator) {
    counters[counter] += accumulator
}

internal fun unbindCounter(counter: Counter) {
    counters.remove(counter)
}

object CounterHandler : Listener {

    /*

    This isn't particularly clean, but I'll refactor it out eventually.

     */

    @EventHandler
    fun handle(event: TriggerDispatchEvent) {
        val trigger = event.trigger
        val data = trigger.data

        val player = trigger.player
        val value = data.value

        val applicableCounters = counters.filter { (counter, _) ->
            counter.trigger == trigger.trigger
        }

        for ((counter, accumulators) in applicableCounters) {
            if (!counter.conditions.areMet(player, data.holder)) {
                continue
            }

            if (!counter.filters.isMet(data)) {
                continue
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
                continue
            }

            val multiplier = if (counter.config.has("multiplier")) {
                config.getDoubleFromExpression("multiplier")
            } else {
                1.0
            }

            met.forEach { it.ifMet(counter, trigger) }
            notMet.forEach { it.ifNotMet(counter, trigger) }

            for (accumulator in accumulators) {
                accumulator.accept(player, value * multiplier)
            }
        }
    }
}
