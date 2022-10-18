package com.willfp.libreforge.chains

import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.NamedArgument
import com.willfp.libreforge.triggers.InvocationData

class EffectChain internal constructor(
    val id: String,
    private val components: Iterable<ChainComponent>
) {
    operator fun invoke(
        invocationData: InvocationData,
        namedArgs: Iterable<NamedArgument>
    ) {
        val chainCompileData = invocationData.compileData as? ChainCompileData ?: return
        chainCompileData.data(invocationData, namedArgs, components)
    }
}

interface ChainComponent {
    operator fun invoke(
        data: InvocationData,
        namedArgs: Iterable<NamedArgument>
    )
}

class ChainComponentEffect(
    val effect: ConfiguredEffect
) : ChainComponent {
    override fun invoke(
        data: InvocationData,
        namedArgs: Iterable<NamedArgument>
    ) {
        effect(data, acceptAllTriggers = true, namedArguments = namedArgs)
    }
}
