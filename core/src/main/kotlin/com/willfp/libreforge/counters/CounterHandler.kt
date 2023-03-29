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
    @EventHandler
    fun handle(event: TriggerDispatchEvent) {
        val dispatch = event.trigger
        val data = dispatch.data

        val player = dispatch.player
        val value = data.value

        val applicableCounters = counters.filter { (counter, _) ->
            counter.trigger == dispatch.trigger
        }

        for ((counter, accumulators) in applicableCounters) {
            if (!counter.conditions.areMet(player, data.holder)) {
                return
            }

            if (!counter.filters.isMet(data)) {
                return
            }

            for (accumulator in accumulators) {
                accumulator.accept(player, value * counter.multiplier)
            }
        }
    }
}
