package com.willfp.libreforge.effects.executors.impl

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.executors.ChainExecutor
import com.willfp.libreforge.effects.executors.ChainExecutorFactory
import com.willfp.libreforge.triggers.DispatchedTrigger
import java.util.concurrent.atomic.AtomicInteger

object CycleExecutorFactory: ChainExecutorFactory("cycle") {
    override fun create() = CycleChainExecutor()

    class CycleChainExecutor : ChainExecutor {
        private val offset = AtomicInteger(0)

        override fun execute(chain: Chain, trigger: DispatchedTrigger): Boolean {
            val index = offset.getAndIncrement() % chain.size
            return chain[index].trigger(trigger)
        }
    }
}
