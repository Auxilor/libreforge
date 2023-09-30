package com.willfp.libreforge.effects

import java.util.UUID

@Deprecated("Use IdentifiedModifier instead", ReplaceWith("IdentifiedModifier"))
class MultiplierModifier(
    val uuid: UUID,
    private val getMultiplier: () -> Double
) {
    val multiplier: Double
        get() = getMultiplier()
}

@Deprecated("Use IdentifiedModifier instead", ReplaceWith("IdentifiedModifier"))
class AdditionModifier(
    val uuid: UUID,
    private val getBonus: () -> Double
) {
    val bonus: Double
        get() = getBonus()
}

class IdentifiedModifier(
    val uuid: UUID,
    private val getModifier: () -> Double
) {
    val modifier: Double
        get() = getModifier()
}
