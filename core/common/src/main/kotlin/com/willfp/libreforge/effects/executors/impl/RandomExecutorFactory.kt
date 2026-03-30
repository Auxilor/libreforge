package com.willfp.libreforge.effects.executors.impl

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.ChainElement
import com.willfp.libreforge.effects.executors.ChainExecutor
import com.willfp.libreforge.effects.executors.ChainExecutorFactory
import com.willfp.libreforge.triggers.DispatchedTrigger

object RandomExecutorFactory : ChainExecutorFactory("random") {
    override fun create() = RandomChainExecutor

    object RandomChainExecutor : ChainExecutor {
        override fun execute(chain: Chain, trigger: DispatchedTrigger): Boolean {
            val element = weightedRandom(chain, trigger) ?: return false
            return element.trigger(trigger)
        }

        private fun weightedRandom(chain: Chain, trigger: DispatchedTrigger): ChainElement<*>? {
            if (chain.isEmpty()) {
                return null
            }

            val totalWeight = chain.sumOf { it.getWeight(trigger).coerceAtLeast(0.0) }

            if (totalWeight == 0.0) {
                val randomIndex = (Math.random() * chain.size).toInt()
                return chain[randomIndex]
            }

            val random = Math.random() * totalWeight
            var current = 0.0
            for (item in chain) {
                current += item.getWeight(trigger).coerceAtLeast(0.0)
                if (random < current) {
                    return item
                }
            }

            return chain.lastOrNull { it.getWeight(trigger) > 0.0 }
        }
    }
}
