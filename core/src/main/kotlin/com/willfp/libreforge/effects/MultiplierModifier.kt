package com.willfp.libreforge.effects

import java.util.UUID

abstract class MultiplierModifier(
    val uuid: UUID
) {
    abstract val multiplier: Double
}
