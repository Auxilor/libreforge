package com.willfp.libreforge.counters

import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.triggers.Trigger

open class UndefinedCounter internal constructor(
    val trigger: Trigger,
    val multiplier: Double,
    val conditions: ConditionList,
    val filters: FilterList
) {
    /**
     * Define a counter with a [count] function.
     */
    fun define(count: (Double) -> Unit): Counter {
        return Counter(trigger, multiplier, conditions, filters, count)
    }
}
