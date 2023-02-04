package com.willfp.libreforge.effects

import java.util.UUID

abstract class MultiplierModifier(
    val uuid: UUID
) {
    val multiplier: Double
        get() = getMultiplier()

    protected abstract fun getMultiplier(): Double
}
