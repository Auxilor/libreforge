package com.willfp.libreforge.chains

import com.willfp.libreforge.effects.CompileData

interface ChainCompileData : CompileData {
    override val data: ChainInvoker
}

object NormalChainCompileData : ChainCompileData {
    override val data = NormalChainInvoker
}

class CycleChainCompileData : ChainCompileData {
    override val data = CycleChainInvoker()
}

class RandomChainCompileData : ChainCompileData {
    override val data = RandomChainInvoker
}
