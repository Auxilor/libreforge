package com.willfp.libreforge.effects.executors.impl

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.ChainElement
import com.willfp.libreforge.effects.executors.ChainExecutor
import com.willfp.libreforge.effects.executors.ChainExecutorFactory
import com.willfp.libreforge.toWeightedList
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.Bukkit
import java.util.Random
import java.util.TreeMap

object RandomExecutorFactory : ChainExecutorFactory("random") {
    override fun create() = RandomChainExecutor

    val random = Random()

    object RandomChainExecutor : ChainExecutor {
        override fun execute(chain: Chain, trigger: DispatchedTrigger): Boolean {
            val element = chain.toWeightedList().randomOrNull()
            if (element != null) {
                if (element.trigger(trigger)) return true
            }
            return false
        }
    }
}
