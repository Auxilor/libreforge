package com.willfp.libreforge.effects.argument

data class EffectArgumentResponse(
    val wasMet: Boolean,
    val met: Collection<EffectArgumentBlock<*>>,
    val notMet: Collection<EffectArgumentBlock<*>>
)
