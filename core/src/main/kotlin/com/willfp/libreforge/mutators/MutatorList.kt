package com.willfp.libreforge.mutators

import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.triggers.TriggerData

/**
 * A list of mutators.
 */
class MutatorList(
    mutators: List<MutatorBlock<*>>
) : DelegatedList<MutatorBlock<*>>(
    mutators.sortedBy {
        it.mutator.runOrder.weight
    }
) {
    fun mutate(data: TriggerData): TriggerData {
        var current = data

        for (block in this) {
            current = block.mutate(current)
        }

        return current
    }
}
