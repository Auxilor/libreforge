package com.willfp.libreforge.dates

import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap

/**
 * A named date or date range loaded from a [DateCalendar] file, e.g. a holiday or a season.
 *
 * Multi-day entries are just entries that are on for more than one day in a row;
 * an entry starts on any day it is on but wasn't on the day before.
 */
abstract class DateEntry(
    /**
     * The config ID, e.g. `christmas_day`, `advent` or `winter`.
     */
    val id: String,
    private val rule: DateRule
) {
    private val cache = ConcurrentHashMap<LocalDate, Boolean>()

    /**
     * If this is on for [date].
     */
    fun isOn(date: LocalDate): Boolean = cache.computeIfAbsent(date) { rule.isOn(it) }

    /**
     * If this begins on [date].
     */
    fun startsOn(date: LocalDate): Boolean = isOn(date) && !isOn(date.minusDays(1))

    /**
     * If this ends on [date], i.e. it is on for [date] but not the day after.
     */
    fun endsOn(date: LocalDate): Boolean = isOn(date) && !isOn(date.plusDays(1))

    /**
     * If this is on today in [zone] (defaults to the zone set in config.yml).
     */
    @JvmOverloads
    fun isToday(zone: ZoneId = Dates.zone): Boolean = isOn(LocalDate.now(zone))

    override fun toString(): String = "${javaClass.simpleName}($id)"
}
