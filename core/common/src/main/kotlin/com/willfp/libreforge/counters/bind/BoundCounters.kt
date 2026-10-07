package com.willfp.libreforge.counters.bind

import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.counters.Accumulator
import com.willfp.libreforge.counters.Counter
import com.willfp.libreforge.triggers.Trigger

internal object BoundCounters {
    private val lock = Any()
    private val bindings = listMap<Counter, BoundCounter>()

    // Immutable once built, so triggers only take the lock after a change.
    @Volatile
    private var byTrigger: Map<Trigger, List<BoundCounter>>? = null

    fun bind(counter: Counter, accumulator: Accumulator) {
        synchronized(lock) {
            bindings[counter].add(BoundCounter(counter, accumulator))
            byTrigger = null
        }
    }

    fun unbind(counter: Counter) {
        synchronized(lock) {
            bindings.remove(counter)
            byTrigger = null
        }
    }

    fun bindingsFor(trigger: Trigger): List<BoundCounter> {
        val index = byTrigger ?: synchronized(lock) {
            byTrigger ?: bindings.values.flatten().groupBy { it.counter.trigger }.also { byTrigger = it }
        }

        return index[trigger].orEmpty()
    }

    fun anyCanBeTriggeredBy(trigger: Trigger): Boolean =
        bindingsFor(trigger).isNotEmpty()
}
