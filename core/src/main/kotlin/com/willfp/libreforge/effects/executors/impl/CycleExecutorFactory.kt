package com.willfp.libreforge.effects.executors.impl

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.executors.ChainExecutor
import com.willfp.libreforge.effects.executors.ChainExecutorFactory
import com.willfp.libreforge.triggers.DispatchedTrigger

object CycleExecutorFactory: ChainExecutorFactory("cycle") {
    override fun create() = CycleChainExecutor()

    class CycleChainExecutor : ChainExecutor {
        private var offset = 0

        override fun execute(chain: Chain, trigger: DispatchedTrigger): Boolean {
            offset %= chain.size
            val success = chain[offset].trigger(trigger)
            offset++
            return success
        }
    }
}
