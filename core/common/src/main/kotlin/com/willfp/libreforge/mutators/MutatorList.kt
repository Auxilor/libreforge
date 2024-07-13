package com.willfp.libreforge.mutators

import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

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
        return this.fold(data) { currentData, block ->
            block.mutate(currentData)
        }
    }

    fun transform(parameters: Set<TriggerParameter>): Set<TriggerParameter> {
        return this.fold(parameters) { currentParameters, block ->
            block.transform(currentParameters)
        }
    }
}

/**
 * Creates an empty [MutatorList].
 */
fun emptyMutatorList() = MutatorList(emptyList())
