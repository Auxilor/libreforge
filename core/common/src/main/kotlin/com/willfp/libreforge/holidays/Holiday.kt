package com.willfp.libreforge.holidays

import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap

/**
 * A holiday or holiday period defined in holidays.yml.
 *
 * Multi-day holidays are just holidays that are on for more than one day in a row;
 * a holiday starts on any day it is on but wasn't on the day before.
 */
class Holiday(
    /**
     * The config ID, e.g. `christmas_day` or `advent`.
     */
    val id: String,
    private val rule: HolidayRule
) {
    private val cache = ConcurrentHashMap<LocalDate, Boolean>()

    /**
     * If this holiday is on for [date].
     */
    fun isOn(date: LocalDate): Boolean = cache.computeIfAbsent(date) { rule.isOn(it) }

    /**
     * If this holiday begins on [date].
     */
    fun startsOn(date: LocalDate): Boolean = isOn(date) && !isOn(date.minusDays(1))

    /**
     * If this holiday ends on [date], i.e. it is on for [date] but not the day after.
     */
    fun endsOn(date: LocalDate): Boolean = isOn(date) && !isOn(date.plusDays(1))

    /**
     * If this holiday is today in [zone] (defaults to the zone set in config.yml).
     */
    @JvmOverloads
    fun isToday(zone: ZoneId = Holidays.zone): Boolean = isOn(LocalDate.now(zone))

    override fun toString(): String = "Holiday($id)"
}
