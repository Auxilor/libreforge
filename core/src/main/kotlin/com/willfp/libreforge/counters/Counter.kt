package com.willfp.libreforge.counters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.counters.bind.BoundCounters
import com.willfp.libreforge.effects.arguments.EffectArgumentList
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.triggers.PotentiallyTriggerable
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
) : ConfigurableElement, PotentiallyTriggerable, Cloneable {
    override val uuid: UUID = UUID.randomUUID()

    /**
     * Bind this counter to an [accumulator].
     */
    fun bind(accumulator: Accumulator) {
        BoundCounters.bind(this, accumulator)
    }

    /**
     * Unbind this counter from all accumulators.
     */
    fun unbind() {
        BoundCounters.unbind(this)
    }

    override fun canBeTriggeredBy(trigger: Trigger) =
        this.trigger == trigger

    public override fun clone(): Counter {
        return Counter(
            trigger,
            conditions,
            filters,
            config.clone(),
            arguments,
            multiplierExpression
        )
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
