package com.willfp.libreforge.effects.impl

import kotlin.math.roundToInt

private const val FORTUNE_ROLL_OFFSET = 1.1

/**
 * The number of items a drop becomes with a simulated [fortune] level, never less than one.
 */
internal fun fortuneDropAmount(fortune: Int): Int =
    if (fortune <= 0) 1 else (Math.random() * (fortune - 1) + FORTUNE_ROLL_OFFSET).roundToInt().coerceAtLeast(1)
