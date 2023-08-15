package com.willfp.libreforge.effects.arguments.custom

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects

class CustomEffectArgument(
    override val id: String,
    config: Config,
    plugin: EcoPlugin
): KRegistrable {
    private val baseContext = ViolationContext(plugin, "custom effect argument $id")

    val isMet = Conditions.compile(
        config.getSubsections("is-met"),
        baseContext.with("is-met")
    )

    val ifMet = Effects.compileChain(
        config.getSubsections("if-met"),
        baseContext.with("if-met")
    )

    val ifNotMet = Effects.compileChain(
        config.getSubsections("if-not-met"),
        baseContext.with("if-not-met")
    )
}
