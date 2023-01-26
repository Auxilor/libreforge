package com.willfp.libreforge.effects.argument

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfiguredProperty
import com.willfp.libreforge.effects.ElementLike
import com.willfp.libreforge.triggers.DispatchedTrigger

/**
 * A single filter config block.
 */
class EffectArgumentBlock<T>(
    val argument: EffectArgument<T>,
    override val config: Config,
    override val compileData: T?
) : ConfiguredProperty<T> {
    fun isMet(
        element: ElementLike,
        trigger: DispatchedTrigger
    ) = argument.isMet(element, trigger, this.compileData)

    fun ifMet(
        element: ElementLike,
        trigger: DispatchedTrigger
    ) = argument.ifMet(element, trigger, this.compileData)

    fun ifNotMet(
        element: ElementLike,
        trigger: DispatchedTrigger
    ) = argument.ifNotMet(element, trigger, this.compileData)
}
