package com.willfp.libreforge.counters

import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.triggers.Trigger
import java.util.Objects
import java.util.UUID

class Counter(
    val trigger: Trigger,
    val multiplier: Double,
    val conditions: ConditionList,
    val filters: FilterList
) {
    private val uuid = UUID.randomUUID()

    /**
     * Bind this counter to an [accumulator].
     */
    fun bind(accumulator: Accumulator) {
        bindCounter(this, accumulator)
    }

    /**
     * Unbind this counter from all accumulators.
     */
    fun unbind() {
        unbindCounter(this)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Counter) {
            return false
        }

        return other.uuid == this.uuid
    }

    override fun hashCode(): Int {
        return Objects.hash(uuid)
    }
}
