package com.willfp.libreforge.counters.bind

import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.counters.Accumulator
import com.willfp.libreforge.counters.Counter

internal object BoundCounters {
    private val bindings = listMap<Counter, BoundCounter>()

    fun bind(counter: Counter, accumulator: Accumulator) {
        bindings[counter] += BoundCounter(counter, accumulator)
    }

    fun unbind(counter: Counter) {
        bindings.remove(counter)
    }

    fun values(): List<Counter> =
        bindings.keys.toList()

    val Counter.bindings: List<BoundCounter>
        get() = BoundCounters.bindings[this].toList()
}
