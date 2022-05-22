package com.willfp.libreforge.chains

import com.willfp.libreforge.effects.NamedArgument
import com.willfp.libreforge.triggers.InvocationData


interface ChainInvoker {
    operator fun invoke(
        invocationData: InvocationData,
        namedArgs: Iterable<NamedArgument>,
        components: Iterable<ChainComponent>
    )
}

object NormalChainInvoker : ChainInvoker {
    override fun invoke(
        invocationData: InvocationData,
        namedArgs: Iterable<NamedArgument>,
        components: Iterable<ChainComponent>
    ) {
        for (component in components) {
            component(invocationData, namedArgs)
        }
    }
}

class CycleChainInvoker : ChainInvoker {
    private var offset = 0
    override fun invoke(
        invocationData: InvocationData,
        namedArgs: Iterable<NamedArgument>,
        components: Iterable<ChainComponent>
    ) {
        val componentList = components.toList()
        val length = componentList.size
        val index = offset % length
        componentList[index](invocationData, namedArgs)
        offset++
    }
}
