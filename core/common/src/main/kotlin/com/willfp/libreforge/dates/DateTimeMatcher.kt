package com.willfp.libreforge.dates

import java.time.DateTimeException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.MonthDay
import java.time.temporal.ChronoUnit

/**
 * Checks a date and time against something compiled from config, e.g. a list of months.
 */
fun interface DateTimeMatcher {
    fun matches(dateTime: LocalDateTime): Boolean
}

/**
 * Compiles config values into [DateTimeMatcher]s.
 *
 * Every function throws [IllegalArgumentException] with a message fit for a config violation
 * when given a value it can't read.
 */
object DateTimeMatchers {
    private val annualDatePattern = Regex("(\\d{1,2})-(\\d{1,2})")
    private val exactDatePattern = Regex("(\\d{4})-(\\d{1,2})-(\\d{1,2})")
    private val timePattern = Regex("(\\d{1,2}):(\\d{2})")

    /**
     * Months as names (`december`) or numbers (`12`).
     */
    fun months(values: List<String>): DateTimeMatcher {
        val months = parseAll(values) { parseEnum(it, "month", 1..12, Month::of) }
        return DateTimeMatcher { it.month in months }
    }

    /**
     * Days of the week as names (`friday`) or ISO numbers (Monday is `1`).
     */
    fun daysOfWeek(values: List<String>): DateTimeMatcher {
        val days = parseAll(values) { parseEnum(it, "day of the week", 1..7, DayOfWeek::of) }
        return DateTimeMatcher { it.dayOfWeek in days }
    }

    /**
     * Days of the month, `1` to `31`.
     */
    fun daysOfMonth(values: List<String>): DateTimeMatcher {
        val days = parseAll(values) { parseNumber(it, "day of the month", 1..31) }
        return DateTimeMatcher { it.dayOfMonth in days }
    }

    /**
     * Dates as `MM-DD` (every year) or `YYYY-MM-DD` (once).
     */
    fun dates(values: List<String>): DateTimeMatcher {
        val dates = parseAll(values) { parseDate(it) }
        return DateTimeMatcher { dateTime -> dates.any { it.matches(dateTime.toLocalDate()) } }
    }

    /**
     * Dates from [from] to [to] inclusive. Both must be `MM-DD` (wrapping across new year if
     * [to] is earlier) or both `YYYY-MM-DD`.
     */
    fun dateBetween(from: String, to: String): DateTimeMatcher {
        val start = parseDate(from)
        val end = parseDate(to)

        if (start is DatePattern.Annual && end is DatePattern.Annual) {
            return DateTimeMatcher {
                MonthDay.from(it).isBetweenWrapping(start.monthDay, end.monthDay)
            }
        }

        if (start is DatePattern.Exact && end is DatePattern.Exact) {
            require(!end.date.isBefore(start.date)) { "Date range ends ($to) before it starts ($from)" }
            return DateTimeMatcher { !it.toLocalDate().isBefore(start.date) && !it.toLocalDate().isAfter(end.date) }
        }

        throw IllegalArgumentException("Dates $from and $to must use the same format, either MM-DD or YYYY-MM-DD")
    }

    /**
     * Hours of the day, `0` to `23`.
     */
    fun hours(values: List<String>): DateTimeMatcher {
        val hours = parseAll(values) { parseNumber(it, "hour", 0..23) }
        return DateTimeMatcher { it.hour in hours }
    }

    /**
     * Hours from [from] to the end of [to] inclusive, wrapping across midnight if [to] is earlier.
     */
    fun hourBetween(from: String, to: String): DateTimeMatcher {
        val startHour = parseNumber(from, "hour", 0..23)
        val endHour = parseNumber(to, "hour", 0..23)
        return timeSpan(LocalTime.of(startHour, 0), LocalTime.of(endHour, 59))
    }

    /**
     * Times (`HH:MM`) from [from] to the end of the minute [to] inclusive, wrapping across
     * midnight if [to] is earlier.
     */
    fun timeBetween(from: String, to: String): DateTimeMatcher =
        timeSpan(parseTime(from), parseTime(to))

    private fun timeSpan(start: LocalTime, end: LocalTime): DateTimeMatcher = DateTimeMatcher {
        it.toLocalTime().truncatedTo(ChronoUnit.MINUTES).isBetweenWrapping(start, end)
    }

    private fun <T : Comparable<T>> T.isBetweenWrapping(start: T, end: T): Boolean =
        if (start <= end) {
            this >= start && this <= end
        } else {
            this >= start || this <= end
        }

    private fun <T> parseAll(values: List<String>, parse: (String) -> T): Set<T> {
        require(values.isNotEmpty()) { "No values given" }
        return values.map { parse(it.trim()) }.toSet()
    }

    private fun parseNumber(value: String, name: String, range: IntRange): Int {
        val number = value.trim().toIntOrNull()
        require(number != null && number in range) {
            "Invalid $name '$value': must be a number from ${range.first} to ${range.last}"
        }
        return number
    }

    private inline fun <reified T : Enum<T>> parseEnum(
        value: String,
        name: String,
        range: IntRange,
        ofNumber: (Int) -> T
    ): T {
        value.toIntOrNull()?.let {
            require(it in range) { "Invalid $name '$value': must be a name or a number from ${range.first} to ${range.last}" }
            return ofNumber(it)
        }

        return enumValues<T>().firstOrNull { it.name.equals(value, ignoreCase = true) }
            ?: throw IllegalArgumentException(
                "Invalid $name '$value': must be one of ${enumValues<T>().joinToString { it.name.lowercase() }}, " +
                        "or a number from ${range.first} to ${range.last}"
            )
    }

    private fun parseDate(value: String): DatePattern {
        val trimmed = value.trim()

        try {
            annualDatePattern.matchEntire(trimmed)?.let { match ->
                val (month, day) = match.destructured
                return DatePattern.Annual(MonthDay.of(month.toInt(), day.toInt()))
            }

            exactDatePattern.matchEntire(trimmed)?.let { match ->
                val (year, month, day) = match.destructured
                return DatePattern.Exact(LocalDate.of(year.toInt(), month.toInt(), day.toInt()))
            }
        } catch (e: DateTimeException) {
            throw IllegalArgumentException("Invalid date '$value': ${e.message}")
        }

        throw IllegalArgumentException("Invalid date '$value': must be MM-DD or YYYY-MM-DD")
    }

    private fun parseTime(value: String): LocalTime {
        val match = timePattern.matchEntire(value.trim())
            ?: throw IllegalArgumentException("Invalid time '$value': must be HH:MM")

        val (hour, minute) = match.destructured
        try {
            return LocalTime.of(hour.toInt(), minute.toInt())
        } catch (e: DateTimeException) {
            throw IllegalArgumentException("Invalid time '$value': ${e.message}")
        }
    }

    private sealed interface DatePattern {
        fun matches(date: LocalDate): Boolean

        /**
         * The same day every year. February 29th only matches in leap years.
         */
        data class Annual(val monthDay: MonthDay) : DatePattern {
            override fun matches(date: LocalDate): Boolean = MonthDay.from(date) == monthDay
        }

        /**
         * A single day.
         */
        data class Exact(val date: LocalDate) : DatePattern {
            override fun matches(date: LocalDate): Boolean = date == this.date
        }
    }
}
