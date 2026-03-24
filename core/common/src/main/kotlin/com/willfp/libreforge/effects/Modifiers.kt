package com.willfp.libreforge.effects

import java.util.UUID

class IdentifiedModifier(
    val uuid: UUID,
    private val getModifier: () -> Double
) {
    val modifier: Double
        get() = getModifier()
}
