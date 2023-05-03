package com.willfp.libreforge.effects

import java.util.UUID

class MultiplierModifier(
    val uuid: UUID,
    private val getMultiplier: () -> Double
) {
    val multiplier: Double
        get() = getMultiplier()
}

class AdditionModifier(
    val uuid: UUID,
    private val getBonus: () -> Double
) {
    val bonus: Double
        get() = getBonus()
}
