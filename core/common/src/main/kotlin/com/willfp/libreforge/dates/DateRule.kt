package com.willfp.libreforge.dates

import java.time.LocalDate

/**
 * How a [DateEntry] decides which dates it is on.
 */
fun interface DateRule {
    fun isOn(date: LocalDate): Boolean

    /**
     * If this begins on [date], i.e. it is on for [date] but not the day before.
     */
    fun startsOn(date: LocalDate): Boolean = isOn(date) && !isOn(date.minusDays(1))

    /**
     * If this ends on [date], i.e. it is on for [date] but not the day after.
     */
    fun endsOn(date: LocalDate): Boolean = isOn(date) && !isOn(date.plusDays(1))

    /**
     * On whenever [matcher] matches the date.
     */
    class Matching(
        private val matcher: DateTimeMatcher
    ) : DateRule {
        override fun isOn(date: LocalDate): Boolean = matcher.matches(date.atStartOfDay())
    }

    /**
     * On whenever every one of [rules] is.
     */
    class AllOf(
        private val rules: List<DateRule>
    ) : DateRule {
        override fun isOn(date: LocalDate): Boolean = rules.all { it.isOn(date) }
    }

    /**
     * On whenever any of [rules] is.
     */
    class AnyOf(
        private val rules: List<DateRule>
    ) : DateRule {
        override fun isOn(date: LocalDate): Boolean = rules.any { it.isOn(date) }
    }

    /**
     * On [days] days after each day [rule] is on (before, if negative).
     */
    class Offset(
        private val rule: DateRule,
        private val days: Long
    ) : DateRule {
        override fun isOn(date: LocalDate): Boolean = rule.isOn(date.minusDays(days))
    }

    /**
     * On whenever [entry] is.
     */
    class Reference(
        private val entry: DateEntry
    ) : DateRule {
        override fun isOn(date: LocalDate): Boolean = entry.isOn(date)
    }

    /**
     * On from each start of [from] up to and including the next end of [to].
     */
    class Range(
        private val from: DateRule,
        private val to: DateRule
    ) : DateRule {
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
