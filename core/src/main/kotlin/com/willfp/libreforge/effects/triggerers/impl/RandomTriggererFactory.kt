package com.willfp.libreforge.effects.triggerers.impl

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.triggerers.ChainTriggerer
import com.willfp.libreforge.effects.triggerers.ChainTriggererFactory
import com.willfp.libreforge.triggers.DispatchedTrigger

object RandomTriggererFactory : ChainTriggererFactory("random") {
    override fun create() = RandomChainTriggerer

    object RandomChainTriggerer : ChainTriggerer {
        override fun trigger(chain: Chain, trigger: DispatchedTrigger) {
            chain.random().trigger(trigger)
        }
    }
}
