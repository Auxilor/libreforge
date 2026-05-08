package com.willfp.libreforge.counters.bind

import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.counters.Accumulator
import com.willfp.libreforge.counters.Counter
import com.willfp.libreforge.triggers.Trigger

internal object BoundCounters {
    private val lock = Any()
    private var bindings = listMap<Counter, BoundCounter>()
    private var cachedValues: Set<Counter>? = null
    private var cachedBindings = HashMap<Counter, List<BoundCounter>>()

    fun bind(counter: Counter, accumulator: Accumulator) {
        synchronized(lock) {
            bindings[counter].add(BoundCounter(counter, accumulator))
            cachedValues = null
            cachedBindings.remove(counter)
        }
    }

    fun unbind(counter: Counter) {
        synchronized(lock) {
            bindings.remove(counter)
            cachedValues = null
            cachedBindings.remove(counter)
        }
    }

    fun values(): Set<Counter> = synchronized(lock) {
        cachedValues ?: bindings.keys.toSet().also { cachedValues = it }
    }

    fun anyCanBeTriggeredBy(trigger: Trigger): Boolean = synchronized(lock) {
        bindings.keys.any { it.canBeTriggeredBy(trigger) }
    }

    val Counter.bindings: List<BoundCounter>
        get() = synchronized(lock) {
            cachedBindings.getOrPut(this) { BoundCounters.bindings[this].toList() }
        }
}
