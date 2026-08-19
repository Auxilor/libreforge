package com.willfp.libreforge.mutators.impl

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToLong

/**
 * Round a [value] to a whole number using a given [mode].
 */
internal fun roundValue(value: Double, mode: String) = when (mode.lowercase()) {
    "up" -> ceil(value)
    "down" -> floor(value)
    else -> value.roundToLong().toDouble()
}
