package com.willfp.libreforge.holidays

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.MonthDay
import java.time.temporal.TemporalAdjusters

internal fun adventSunday(year: Int): LocalDate =
    LocalDate.of(year, Month.DECEMBER, 24)
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        .minusWeeks(3)

internal fun fixed(month: Month, day: Int): (Int) -> LocalDate {
    val monthDay = MonthDay.of(month, day)
    return { monthDay.atYear(it) }
}

internal fun easter(offset: Long): (Int) -> LocalDate =
    { Holidays.easterSunday(it).plusDays(offset) }

internal fun weekday(month: Month, ordinal: Int, dayOfWeek: DayOfWeek): (Int) -> LocalDate = {
    val first = LocalDate.of(it, month, 1)
    if (ordinal > 0) {
        first.with(TemporalAdjusters.dayOfWeekInMonth(ordinal, dayOfWeek))
    } else {
        first.with(TemporalAdjusters.lastInMonth(dayOfWeek)).minusWeeks((-ordinal - 1).toLong())
    }
}
