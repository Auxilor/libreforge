package com.willfp.libreforge.effects.impl.aoe

import com.willfp.libreforge.effects.Chain

data class AOECompileData(
    val shape: AOEBlock<*>?,
    val chain: Chain?
)
