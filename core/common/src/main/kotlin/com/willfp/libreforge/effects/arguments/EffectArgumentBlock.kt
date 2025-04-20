package com.willfp.libreforge.effects.arguments

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.triggers.DispatchedTrigger

/**
 * A single argument config block.
 */
class EffectArgumentBlock<T>(
    val argument: EffectArgument<T>,
    override val config: Config,
    override val compileData: T
) : Compiled<T> {
    fun isMet(
        element: ConfigurableElement,
        trigger: DispatchedTrigger
    ) = argument.isMet(element, trigger, this.compileData)

    fun ifMet(
        element: ConfigurableElement,
        trigger: DispatchedTrigger
    ) = argument.ifMet(element, trigger, this.compileData)

    fun ifNotMet(
        element: ConfigurableElement,
        trigger: DispatchedTrigger
    ) = argument.ifNotMet(element, trigger, this.compileData)
}
