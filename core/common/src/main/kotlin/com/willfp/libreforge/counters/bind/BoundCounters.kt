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

    // Bindings indexed by the trigger their counter listens to, so a dispatch only visits its own.
    private val bindingsByTrigger = listMap<Trigger, BoundCounter>()
    private val cachedByTrigger = HashMap<Trigger, List<BoundCounter>>()

    fun bind(counter: Counter, accumulator: Accumulator) {
        synchronized(lock) {
            val bound = BoundCounter(counter, accumulator)
            bindings[counter].add(bound)
            bindingsByTrigger[counter.trigger].add(bound)
            cachedValues = null
            cachedBindings.remove(counter)
            cachedByTrigger.remove(counter.trigger)
        }
    }

    fun unbind(counter: Counter) {
        synchronized(lock) {
            bindings.remove(counter)
            bindingsByTrigger[counter.trigger].removeAll { it.counter == counter }
            if (bindingsByTrigger[counter.trigger].isEmpty()) {
                bindingsByTrigger.remove(counter.trigger)
            }
            cachedValues = null
            cachedBindings.remove(counter)
            cachedByTrigger.remove(counter.trigger)
        }
    }

    fun values(): Set<Counter> = synchronized(lock) {
        cachedValues ?: bindings.keys.toSet().also { cachedValues = it }
    }

    fun anyCanBeTriggeredBy(trigger: Trigger): Boolean =
        bindingsFor(trigger).isNotEmpty()

    /**
     * The bindings whose counter listens to [trigger].
     */
    fun bindingsFor(trigger: Trigger): List<BoundCounter> = synchronized(lock) {
        cachedByTrigger.getOrPut(trigger) {
            bindingsByTrigger[trigger].toList().also {
                if (it.isEmpty()) {
                    bindingsByTrigger.remove(trigger)
                }
            }
        }
    }

    val Counter.bindings: List<BoundCounter>
        get() = synchronized(lock) {
            cachedBindings.getOrPut(this) { BoundCounters.bindings[this].toList() }
        }
}
