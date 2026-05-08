package com.willfp.libreforge.counters.bind

import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.counters.Accumulator
import com.willfp.libreforge.counters.Counter
import com.willfp.libreforge.triggers.Trigger

internal object BoundCounters {
    private val lock = Any()
    private var bindings = listMap<Counter, BoundCounter>()

    fun bind(counter: Counter, accumulator: Accumulator) {
        synchronized(lock) {
            bindings[counter].add(BoundCounter(counter, accumulator))
        }
    }

    fun unbind(counter: Counter) {
        synchronized(lock) {
            bindings.remove(counter)
        }
    }

    fun values(): Set<Counter> = synchronized(lock) {
        bindings.keys.toSet()
    }

    fun anyCanBeTriggeredBy(trigger: Trigger): Boolean = synchronized(lock) {
        bindings.keys.any { it.canBeTriggeredBy(trigger) }
    }

    val Counter.bindings: List<BoundCounter>
        get() = synchronized(lock) {
            BoundCounters.bindings[this].toList()
        }
}
