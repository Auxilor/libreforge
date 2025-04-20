package com.willfp.libreforge.effects.arguments

data class EffectArgumentResponse(
    val wasMet: Boolean,
    val met: Collection<EffectArgumentBlock<*>>,
    val notMet: Collection<EffectArgumentBlock<*>>
)
