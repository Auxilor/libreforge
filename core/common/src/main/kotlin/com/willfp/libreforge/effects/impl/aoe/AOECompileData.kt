package com.willfp.libreforge.effects.impl.aoe

import com.willfp.libreforge.effects.RichChain

data class AOECompileData(
    val shape: AOEBlock<*>?,
    val chain: RichChain?
)
