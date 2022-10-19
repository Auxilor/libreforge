package com.willfp.libreforge.chains

import com.willfp.libreforge.effects.CompileData

interface ChainCompileData : CompileData {
    val invoker: ChainInvoker
}

object NormalChainCompileData : ChainCompileData {
    override val invoker = NormalChainInvoker
}

class CycleChainCompileData : ChainCompileData {
    override val invoker = CycleChainInvoker()
}

class RandomChainCompileData : ChainCompileData {
    override val invoker = RandomChainInvoker
}
