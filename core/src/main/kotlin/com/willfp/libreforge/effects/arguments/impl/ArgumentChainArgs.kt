package com.willfp.libreforge.effects.arguments.impl

import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.get

object ArgumentChainArgs : EffectArgument<NoCompileData>("chain_args") {
    // Misusing arguments a bit here but it's surprisingly clean
    override fun isMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData) = true

    override fun ifMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val config = element.config.getSubsection("chain-args")

        for (arg in config.getKeys(false)) {
            trigger.addPlaceholder(
                NamedValue(
                    listOf(arg, arg.replace("_", "")),
                    config.getFormattedString(arg, placeholderContext(
                        player = trigger.dispatcher.get(),
                    ))
                )
            )
        }
    }
}
