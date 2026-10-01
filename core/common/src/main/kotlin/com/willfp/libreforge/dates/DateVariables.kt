package com.willfp.libreforge.dates

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * The date placeholders available in holiday and season expressions, e.g. `%month%`.
 */
object DateVariables {
    private val placeholderPattern = Regex("%([a-z_]+)%")

    private val variables: Map<String, (LocalDate) -> Int> = linkedMapOf(
        "day" to { it.dayOfMonth },
        "month" to { it.monthValue },
        "year" to { it.year },
        "weekday" to { it.dayOfWeek.value },
        "day_of_year" to { it.dayOfYear },
        "days_in_month" to { it.lengthOfMonth() },
        "is_leap_year" to { if (it.isLeapYear) 1 else 0 },
        "weekday_ordinal" to { (it.dayOfMonth - 1) / 7 + 1 },
        "weekday_ordinal_from_end" to { (it.lengthOfMonth() - it.dayOfMonth) / 7 + 1 },
        "days_from_easter" to { ChronoUnit.DAYS.between(Dates.easterSunday(it.year), it).toInt() }
    )

    /**
     * The names of every placeholder, without the surrounding `%`.
     */
    val names: Set<String>
        get() = variables.keys

    /**
     * The placeholder names used in [expression] that don't exist.
     */
    fun unknownIn(expression: String): Set<String> =
        placeholderPattern.findAll(expression)
            .map { it.groupValues[1] }
            .filterNot { it in variables }
            .toSet()

    /**
     * [expression] with every known placeholder replaced by its value on [date].
     */
    fun substitute(expression: String, date: LocalDate): String =
        placeholderPattern.replace(expression) { match ->
            val variable = variables[match.groupValues[1]] ?: return@replace match.value
            // Bracketed so negative values can't merge with a preceding operator
            "(${variable(date)})"
        }
}
