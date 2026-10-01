package com.willfp.libreforge.holidays

import com.willfp.libreforge.dates.DateCompiler
import com.willfp.libreforge.dates.DateDefinition
import com.willfp.libreforge.dates.Dates
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.MonthDay
import java.time.temporal.TemporalAdjusters

class HolidayTest {
    @Test
    fun easterSundayKnownYears() {
        assertEquals(LocalDate.of(2024, 3, 31), Dates.easterSunday(2024))
        assertEquals(LocalDate.of(2025, 4, 20), Dates.easterSunday(2025))
        assertEquals(LocalDate.of(2026, 4, 5), Dates.easterSunday(2026))
        assertEquals(LocalDate.of(2038, 4, 25), Dates.easterSunday(2038))
        assertEquals(LocalDate.of(2285, 3, 22), Dates.easterSunday(2285))
    }

    @Test
    fun defaultFileLoadsCleanly() {
        val ids = DefaultHolidays.entries.keys
        assertEquals(emptyList<String>(), DefaultHolidays.warnings)
        assertEquals(
            setOf(
                "new_years_day", "valentines_day", "easter_sunday", "thanksgiving", "black_friday",
                "christmas_eve", "christmas_day", "boxing_day", "new_years_eve"
            ),
            ids
        )
    }

    /**
     * Every default holiday, and every example in holidays.yml's comments, is on exactly
     * the days the old hardcoded rules gave.
     */
    @Test
    fun matchesLegacyRules() {
        val examples = DateCompiler("holiday", ::Holiday) { throw AssertionError(it) }
            .compile(exampleDefinitions)
            .associateBy { it.id }
        val holidays = DefaultHolidays.entries + examples

        val start = LocalDate.of(2000, 1, 1)
        val end = LocalDate.of(2040, 12, 31)

        for ((id, holiday) in holidays) {
            val legacyIsOn = legacyRules.getValue(id)
            for (date in start.datesUntil(end.plusDays(1))) {
                assertEquals(legacyIsOn(date), holiday.isOn(date), "$id on $date")
            }
        }
    }

    @Test
    fun blackFriday2026() {
        val blackFriday = DefaultHolidays["black_friday"]
        assertTrue(blackFriday.startsOn(LocalDate.of(2026, 11, 27)))
        assertFalse(blackFriday.isOn(LocalDate.of(2026, 11, 26)))
        assertFalse(blackFriday.isOn(LocalDate.of(2026, 11, 28)))
    }

    private companion object {
        // The rules holidays were hardcoded with before holidays.yml
        fun fixed(month: Month, day: Int): (LocalDate) -> Boolean =
            { MonthDay.from(it) == MonthDay.of(month, day) }

        fun easter(offset: Long): (LocalDate) -> Boolean =
            { Dates.easterSunday(it.year).plusDays(offset) == it }

        fun weekdayDate(year: Int, month: Month, ordinal: Int, dayOfWeek: DayOfWeek): LocalDate {
            val first = LocalDate.of(year, month, 1)
            return if (ordinal > 0) {
                first.with(TemporalAdjusters.dayOfWeekInMonth(ordinal, dayOfWeek))
            } else {
                first.with(TemporalAdjusters.lastInMonth(dayOfWeek)).minusWeeks((-ordinal - 1).toLong())
            }
        }

        fun weekday(month: Month, ordinal: Int, dayOfWeek: DayOfWeek): (LocalDate) -> Boolean =
            { weekdayDate(it.year, month, ordinal, dayOfWeek) == it }

        fun thanksgiving(year: Int) = weekdayDate(year, Month.NOVEMBER, 4, DayOfWeek.THURSDAY)

        fun adventSunday(year: Int): LocalDate =
            LocalDate.of(year, Month.DECEMBER, 24)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                .minusWeeks(3)

        fun period(start: (Int) -> LocalDate, end: (Int) -> LocalDate): (LocalDate) -> Boolean = { date ->
            (date.year - 1..date.year).any { !date.isBefore(start(it)) && !date.isAfter(end(it)) }
        }

        fun easterPeriod(startOffset: Long, endOffset: Long) = period(
            { Dates.easterSunday(it).plusDays(startOffset) },
            { Dates.easterSunday(it).plusDays(endOffset) }
        )

        // Mirrors the commented examples in holidays.yml
        val exampleDefinitions = listOf(
            DateDefinition("good_friday", dates = listOf("%easter%"), dateOffset = "-2"),
            DateDefinition("memorial_day", months = listOf("may"), daysOfWeek = listOf("monday"), weeks = listOf("last")),
            DateDefinition("thanksgiving", months = listOf("november"), daysOfWeek = listOf("thursday"), weeks = listOf("4")),
            DateDefinition("cyber_monday", dates = listOf("%thanksgiving%"), dateOffset = "4"),
            DateDefinition("twelve_days_of_christmas", dates = listOf("%christmas_day%"), dateOffset = "0..11"),
            DateDefinition("christmas_day", dates = listOf("12-25")),
            DateDefinition("advent_sunday", dates = listOf("11-27..12-03"), daysOfWeek = listOf("sunday")),
            DateDefinition("christmas_eve", dates = listOf("12-24")),
            DateDefinition("advent", dates = listOf("%advent_sunday%..%christmas_eve%"))
        )

        val legacyRules: Map<String, (LocalDate) -> Boolean> = mapOf(
            "new_years_day" to fixed(Month.JANUARY, 1),
            "epiphany" to fixed(Month.JANUARY, 6),
            "groundhog_day" to fixed(Month.FEBRUARY, 2),
            "valentines_day" to fixed(Month.FEBRUARY, 14),
            "st_davids_day" to fixed(Month.MARCH, 1),
            "international_womens_day" to fixed(Month.MARCH, 8),
            "st_patricks_day" to fixed(Month.MARCH, 17),
            "april_fools_day" to fixed(Month.APRIL, 1),
            "earth_day" to fixed(Month.APRIL, 22),
            "st_georges_day" to fixed(Month.APRIL, 23),
            "may_day" to fixed(Month.MAY, 1),
            "cinco_de_mayo" to fixed(Month.MAY, 5),
            "juneteenth" to fixed(Month.JUNE, 19),
            "canada_day" to fixed(Month.JULY, 1),
            "independence_day" to fixed(Month.JULY, 4),
            "bastille_day" to fixed(Month.JULY, 14),
            "halloween" to fixed(Month.OCTOBER, 31),
            "all_saints_day" to fixed(Month.NOVEMBER, 1),
            "day_of_the_dead" to fixed(Month.NOVEMBER, 2),
            "guy_fawkes_night" to fixed(Month.NOVEMBER, 5),
            "remembrance_day" to fixed(Month.NOVEMBER, 11),
            "st_andrews_day" to fixed(Month.NOVEMBER, 30),
            "christmas_eve" to fixed(Month.DECEMBER, 24),
            "christmas_day" to fixed(Month.DECEMBER, 25),
            "boxing_day" to fixed(Month.DECEMBER, 26),
            "new_years_eve" to fixed(Month.DECEMBER, 31),
            "shrove_tuesday" to easter(-47),
            "ash_wednesday" to easter(-46),
            "mothering_sunday" to easter(-21),
            "palm_sunday" to easter(-7),
            "good_friday" to easter(-2),
            "easter_sunday" to easter(0),
            "easter_monday" to easter(1),
            "ascension_day" to easter(39),
            "pentecost" to easter(49),
            "whit_monday" to easter(50),
            "corpus_christi" to easter(60),
            "martin_luther_king_day" to weekday(Month.JANUARY, 3, DayOfWeek.MONDAY),
            "presidents_day" to weekday(Month.FEBRUARY, 3, DayOfWeek.MONDAY),
            "early_may_bank_holiday" to weekday(Month.MAY, 1, DayOfWeek.MONDAY),
            "mothers_day" to weekday(Month.MAY, 2, DayOfWeek.SUNDAY),
            "memorial_day" to weekday(Month.MAY, -1, DayOfWeek.MONDAY),
            "spring_bank_holiday" to weekday(Month.MAY, -1, DayOfWeek.MONDAY),
            "fathers_day" to weekday(Month.JUNE, 3, DayOfWeek.SUNDAY),
            "summer_bank_holiday" to weekday(Month.AUGUST, -1, DayOfWeek.MONDAY),
            "labor_day" to weekday(Month.SEPTEMBER, 1, DayOfWeek.MONDAY),
            "columbus_day" to weekday(Month.OCTOBER, 2, DayOfWeek.MONDAY),
            "canadian_thanksgiving" to weekday(Month.OCTOBER, 2, DayOfWeek.MONDAY),
            "thanksgiving" to { thanksgiving(it.year) == it },
            "black_friday" to { thanksgiving(it.year).plusDays(1) == it },
            "advent_sunday" to { adventSunday(it.year) == it },
            "cyber_monday" to { thanksgiving(it.year).plusDays(4) == it },
            "advent" to period(::adventSunday) { LocalDate.of(it, Month.DECEMBER, 24) },
            "twelve_days_of_christmas" to period(
                { LocalDate.of(it, Month.DECEMBER, 25) },
                { LocalDate.of(it + 1, Month.JANUARY, 5) }
            ),
            "lent" to easterPeriod(-46, -1),
            "holy_week" to easterPeriod(-7, -1),
            "eastertide" to easterPeriod(0, 49),
        )
    }
}
