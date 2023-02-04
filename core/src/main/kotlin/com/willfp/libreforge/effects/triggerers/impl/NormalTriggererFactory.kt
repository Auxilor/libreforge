package com.willfp.libreforge.effects.triggerers.impl

import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.triggerers.ChainTriggerer
import com.willfp.libreforge.effects.triggerers.ChainTriggererFactory
import com.willfp.libreforge.triggers.DispatchedTrigger

object NormalTriggererFactory : ChainTriggererFactory("normal") {
    override fun create() = NormalChainTriggerer

    object NormalChainTriggerer : ChainTriggerer {
        override fun trigger(chain: Chain, trigger: DispatchedTrigger): Boolean {
            return chain.all { it.trigger(trigger) }
        }
    }
}
