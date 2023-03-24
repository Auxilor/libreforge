package com.willfp.libreforge.counters

/**
 * Accepts a count produced by a [Counter].
 */
interface Accumulator {
    /**
     * Accept a count.
     */
    fun accept(count: Double)
}
