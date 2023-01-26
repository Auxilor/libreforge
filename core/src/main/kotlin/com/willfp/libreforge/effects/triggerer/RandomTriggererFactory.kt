package com.willfp.libreforge.effects.triggerer

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.triggers.DispatchedTrigger

object RandomTriggererFactory : ChainTriggererFactory("random") {
    override fun create() = RandomChainTriggerer

    object RandomChainTriggerer : ChainTriggerer {
        override fun trigger(chain: Chain, trigger: DispatchedTrigger) {
            chain.random().trigger(trigger)
        }
    }
}
