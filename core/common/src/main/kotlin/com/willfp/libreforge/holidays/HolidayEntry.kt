package com.willfp.libreforge.holidays

import java.time.LocalDate

/**
 * A holiday or holiday period, used to generate the per-holiday conditions, filters and triggers.
 */
interface HolidayEntry {
    /**
     * The config ID, e.g. `christmas_day` or `advent`.
     */
    val id: String

    /**
     * If this is active on [date].
     */
    fun isOn(date: LocalDate): Boolean

    /**
     * If this begins on [date]. For single-day holidays, the same as [isOn].
     */
    fun startsOn(date: LocalDate): Boolean

    companion object {
        /**
         * Every holiday and holiday period.
         */
        @JvmStatic
        val all: List<HolidayEntry>
            get() = Holiday.entries + HolidayPeriod.entries
    }
}
