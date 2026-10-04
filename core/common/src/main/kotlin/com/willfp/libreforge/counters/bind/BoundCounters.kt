package com.willfp.libreforge.counters.bind

import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.counters.Accumulator
import com.willfp.libreforge.counters.Counter
import com.willfp.libreforge.triggers.Trigger

internal object BoundCounters {
    private val lock = Any()
    private var bindings = listMap<Counter, BoundCounter>()
    private var cachedByTrigger: Map<Trigger, List<BoundCounter>>? = null

    fun bind(counter: Counter, accumulator: Accumulator) {
        synchronized(lock) {
            bindings[counter].add(BoundCounter(counter, accumulator))
            cachedByTrigger = null
        }
    }

    fun unbind(counter: Counter) {
        synchronized(lock) {
            bindings.remove(counter)
            cachedByTrigger = null
        }
    }

    fun bindingsFor(trigger: Trigger): List<BoundCounter> = synchronized(lock) {
        val byTrigger = cachedByTrigger
            ?: bindings.values.flatten().groupBy { it.counter.trigger }.also { cachedByTrigger = it }

        byTrigger[trigger].orEmpty()
    }

    fun anyCanBeTriggeredBy(trigger: Trigger): Boolean =
        bindingsFor(trigger).isNotEmpty()
}
