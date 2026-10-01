package com.willfp.libreforge.holidays

import java.time.LocalDate

/**
 * How a [Holiday] decides which dates it is on.
 */
sealed interface HolidayRule {
    fun isOn(date: LocalDate): Boolean

    /**
     * On whenever [expression] evaluates to 1 with the date placeholders filled in.
     */
    class Expression(
        val expression: String,
        private val evaluate: (String) -> Double?
    ) : HolidayRule {
        override fun isOn(date: LocalDate): Boolean =
            evaluate(HolidayDateVariables.substitute(expression, date)) == 1.0
    }

    /**
     * On for each date in [dates].
     */
    class Dates(
        private val dates: Set<LocalDate>
    ) : HolidayRule {
        override fun isOn(date: LocalDate): Boolean = date in dates
    }

    /**
     * On from each start of [from] up to and including the next end of [to].
     */
    class Range(
        private val from: Holiday,
        private val to: Holiday
    ) : HolidayRule {
        override fun isOn(date: LocalDate): Boolean {
            // Walk back to the latest start of [from]; an end of [to] before today means the range already closed
            for (daysBack in 0..MAX_LENGTH_DAYS) {
                val day = date.minusDays(daysBack)

                if (daysBack > 0 && to.endsOn(day)) {
                    return false
                }

                if (from.startsOn(day)) {
                    return true
                }
            }

            return false
        }

        private companion object {
            const val MAX_LENGTH_DAYS = 366L
        }
    }
}
