package com.willfp.libreforge.effects

import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.argument.EffectArgumentList
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.triggers.DispatchedTrigger
import java.util.UUID

/**
 * Things that are like a chain element (e.g. Blocks, Elements).
 */
abstract class ElementLike {
    abstract val uuid: UUID
    abstract val arguments: EffectArgumentList
    abstract val conditions: ConditionList
    abstract val mutators: MutatorList
    abstract val filters: FilterList

    /**
     * Mutate, filter, and then trigger.
     */
    fun trigger(trigger: DispatchedTrigger) {
        if (!conditions.areMet(trigger.player)) {
            return
        }

        val data = mutators.mutate(trigger.data)

        if (!filters.filter(data)) {
            return
        }

        if (!arguments.checkMet(this, trigger)) {
            return
        }

        doTrigger(
            trigger.copy(
                data = data
            )
        )
    }

    protected abstract fun doTrigger(trigger: DispatchedTrigger)
}
