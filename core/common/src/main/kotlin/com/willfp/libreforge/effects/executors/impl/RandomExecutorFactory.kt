package com.willfp.libreforge.effects.executors.impl

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.ChainElement
import com.willfp.libreforge.effects.executors.ChainExecutor
import com.willfp.libreforge.effects.executors.ChainExecutorFactory
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.Bukkit
import java.util.Random
import java.util.TreeMap

object RandomExecutorFactory : ChainExecutorFactory("random") {
    override fun create() = RandomChainExecutor

    val random = Random()

    object RandomChainExecutor : ChainExecutor {
        override fun execute(chain: Chain, trigger: DispatchedTrigger): Boolean {
            if (chain.isEmpty()) return false

            if (chain.size == 1) {
                return chain.first().trigger(trigger)
            }

            val randomMap: TreeMap<Double, ChainElement<*>> = TreeMap()
            chain.forEach {
                var weight = it.weight

                if (weight < 0) weight = 0.0
                if (weight != 0.0) randomMap[weight] = it
            }

            if (randomMap.lastKey() == 0.0) { // If all weights are 0
                return chain.random().trigger(trigger)
            }

            val randomValue = random.nextDouble() * randomMap.lastKey()
            return randomMap.higherEntry(randomValue)?.value?.trigger(trigger) ?: false
        }
    }
}
