package com.willfp.libreforge.effects.executors.impl

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.executors.ChainExecutor
import com.willfp.libreforge.effects.executors.ChainExecutorFactory
import com.willfp.libreforge.triggers.DispatchedTrigger

object NormalExecutorFactory : ChainExecutorFactory("normal") {
    override fun create() = NormalChainExecutor

    object NormalChainExecutor : ChainExecutor {
        override fun execute(chain: Chain, trigger: DispatchedTrigger): Boolean {
            var success = false
            chain.forEach {
                if (it.trigger(trigger)) success = true
            }
            return success
        }
    }
}
