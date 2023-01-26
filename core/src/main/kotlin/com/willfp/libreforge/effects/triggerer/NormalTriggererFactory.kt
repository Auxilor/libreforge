package com.willfp.libreforge.effects.triggerer

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.triggers.DispatchedTrigger

object NormalTriggererFactory: ChainTriggererFactory("normal") {
    override fun create() = NormalChainTriggerer

    object NormalChainTriggerer : ChainTriggerer {
        override fun trigger(chain: Chain, trigger: DispatchedTrigger) {
            for (element in chain) {
                element.trigger(trigger)
            }
        }
    }
}
