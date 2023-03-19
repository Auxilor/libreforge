package com.willfp.libreforge.effects.arguments

import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.effects.ElementLike
import com.willfp.libreforge.triggers.DispatchedTrigger

/**
 * A list of arguments.
 */
class EffectArgumentList(
    arguments: List<EffectArgumentBlock<*>>
) : DelegatedList<EffectArgumentBlock<*>>(
    arguments.sortedBy {
        it.argument.runOrder.weight
    }
) {
    fun checkMet(element: ElementLike, trigger: DispatchedTrigger): EffectArgumentResponse {
        val met = mutableListOf<EffectArgumentBlock<*>>()
        val notMet = mutableListOf<EffectArgumentBlock<*>>()

        for (argument in this) {
            val isMet = argument.isMet(element, trigger)

            if (isMet) {
                met += argument
            } else {
                notMet += argument
            }
        }

        return EffectArgumentResponse(
            notMet.isEmpty(),
            met,
            notMet
        )
    }
}
