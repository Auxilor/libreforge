package com.willfp.libreforge.effects

import java.util.UUID

class MultiplierModifier(
    val uuid: UUID,
    private val getMultiplier: () -> Double
) {
    val multiplier: Double
        get() = getMultiplier()
}
