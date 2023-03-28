package com.willfp.libreforge.effects.arguments.impl

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.libreforge.GroupedStaticPlaceholder
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.ElementLike
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.triggers.DispatchedTrigger

object ArgumentChainArgs : EffectArgument<NoCompileData>("chain_args") {
    // Misusing arguments a bit here but it's surprisingly clean
    override fun isMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData) = true

    override fun ifMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val config = element.config.getSubsection("chain-args")

        for (arg in config.getKeys(false)) {
            trigger.addPlaceholder(
                GroupedStaticPlaceholder(
                    listOf(arg, arg.replace("_", "")),
                    PlaceholderManager.translatePlaceholders(
                        config.getString(arg),
                        trigger.player,
                        config
                    )
                )
            )
        }
    }
}
