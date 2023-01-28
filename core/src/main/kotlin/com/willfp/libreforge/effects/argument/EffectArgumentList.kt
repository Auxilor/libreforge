package com.willfp.libreforge.effects.argument

import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.effects.ElementLike
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.triggers.DispatchedTrigger

/**
 * A list of arguments.
 */
class EffectArgumentList(
    arguments: List<EffectArgumentBlock<*>>
) : DelegatedList<EffectArgumentBlock<*>>() {
    init {
        for (order in RunOrder.values()) {
            this.list += arguments.filter { it.argument.runOrder == order }
        }
    }

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
