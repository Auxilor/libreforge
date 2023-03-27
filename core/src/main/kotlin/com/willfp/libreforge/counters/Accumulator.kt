package com.willfp.libreforge.counters

import org.bukkit.entity.Player

/**
 * Accepts a count produced by a [Counter].
 */
interface Accumulator {
    /**
     * Accept a [count] for a [player].
     */
    fun accept(player: Player, count: Double)
}
