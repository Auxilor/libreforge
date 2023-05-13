package com.willfp.libreforge.counters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.arguments.EffectArgumentList
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.triggers.Trigger
import java.util.Objects
import java.util.UUID

class Counter internal constructor(
    val trigger: Trigger,
    val conditions: ConditionList,
    val filters: FilterList,
    override val config: Config,
    val arguments: EffectArgumentList,
    val multiplierExpression: String
) : ConfigurableElement {
    override val uuid: UUID = UUID.randomUUID()

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
