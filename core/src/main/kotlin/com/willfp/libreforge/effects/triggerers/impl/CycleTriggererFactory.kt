package com.willfp.libreforge.effects.triggerers.impl

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.triggerers.ChainTriggerer
import com.willfp.libreforge.effects.triggerers.ChainTriggererFactory
import com.willfp.libreforge.triggers.DispatchedTrigger

object CycleTriggererFactory: ChainTriggererFactory("cycle") {
    override fun create() = CycleChainTriggerer()

    class CycleChainTriggerer : ChainTriggerer {
        private var offset = 0

        override fun trigger(chain: Chain, trigger: DispatchedTrigger): Boolean {
            offset %= chain.size
            val success = chain[offset].trigger(trigger)
            offset++
            return success
        }
    }
}
