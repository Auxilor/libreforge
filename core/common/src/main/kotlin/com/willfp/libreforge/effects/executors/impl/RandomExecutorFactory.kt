package com.willfp.libreforge.effects.executors.impl

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.executors.ChainExecutor
import com.willfp.libreforge.effects.executors.ChainExecutorFactory
import com.willfp.libreforge.triggers.DispatchedTrigger

object RandomExecutorFactory : ChainExecutorFactory("random") {
    override fun create() = RandomChainExecutor

    object RandomChainExecutor : ChainExecutor {
        override fun execute(chain: Chain, trigger: DispatchedTrigger): Boolean {
           return chain.random().trigger(trigger)
        }
    }
}
