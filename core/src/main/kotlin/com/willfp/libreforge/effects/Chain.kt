package com.willfp.libreforge.effects

import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.effects.triggerer.ChainTriggerer
import com.willfp.libreforge.triggers.DispatchedTrigger

/**
 * A list of effect blocks.
 */
class Chain(
    effects: List<ChainElement<*>>,
    private val triggerer: ChainTriggerer
) : DelegatedList<ChainElement<*>>() {
    init {
        for (order in RunOrder.values()) {
            this.list += effects.filter { it.effect.runOrder == order }
        }
    }

    fun trigger(trigger: DispatchedTrigger) {
        triggerer.trigger(this, trigger)
    }
}
