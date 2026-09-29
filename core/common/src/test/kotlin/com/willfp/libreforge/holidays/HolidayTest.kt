package com.willfp.libreforge.holidays

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class HolidayTest {
    @Test
    fun easterSundayKnownYears() {
        assertEquals(LocalDate.of(2024, 3, 31), Holidays.easterSunday(2024))
        assertEquals(LocalDate.of(2025, 4, 20), Holidays.easterSunday(2025))
        assertEquals(LocalDate.of(2026, 4, 5), Holidays.easterSunday(2026))
        assertEquals(LocalDate.of(2038, 4, 25), Holidays.easterSunday(2038))
        assertEquals(LocalDate.of(2285, 3, 22), Holidays.easterSunday(2285))
    }

    @Test
    fun calculatedDates2026() {
        val expected = mapOf(
            Holiday.SHROVE_TUESDAY to LocalDate.of(2026, 2, 17),
            Holiday.MOTHERING_SUNDAY to LocalDate.of(2026, 3, 15),
            Holiday.GOOD_FRIDAY to LocalDate.of(2026, 4, 3),
            Holiday.ASCENSION_DAY to LocalDate.of(2026, 5, 14),
            Holiday.WHIT_MONDAY to LocalDate.of(2026, 5, 25),
            Holiday.CORPUS_CHRISTI to LocalDate.of(2026, 6, 4),
            Holiday.MARTIN_LUTHER_KING_DAY to LocalDate.of(2026, 1, 19),
            Holiday.PRESIDENTS_DAY to LocalDate.of(2026, 2, 16),
            Holiday.EARLY_MAY_BANK_HOLIDAY to LocalDate.of(2026, 5, 4),
            Holiday.MOTHERS_DAY to LocalDate.of(2026, 5, 10),
            Holiday.MEMORIAL_DAY to LocalDate.of(2026, 5, 25),
            Holiday.FATHERS_DAY to LocalDate.of(2026, 6, 21),
            Holiday.SUMMER_BANK_HOLIDAY to LocalDate.of(2026, 8, 31),
            Holiday.LABOR_DAY to LocalDate.of(2026, 9, 7),
            Holiday.COLUMBUS_DAY to LocalDate.of(2026, 10, 12),
            Holiday.THANKSGIVING to LocalDate.of(2026, 11, 26),
            Holiday.BLACK_FRIDAY to LocalDate.of(2026, 11, 27),
            Holiday.CYBER_MONDAY to LocalDate.of(2026, 11, 30),
        )

        for ((holiday, date) in expected) {
            assertEquals(date, holiday.dateIn(2026), holiday.id)
        }
    }

    @Test
    fun fixedDatesIgnoreWeekends() {
        // Christmas 2027 is a Saturday; must not shift to an observed day
        assertEquals(LocalDate.of(2027, 12, 25), Holiday.CHRISTMAS_DAY.dateIn(2027))
    }

    @Test
    fun lookups() {
        assertTrue(Holiday.CHRISTMAS_DAY in Holiday.on(LocalDate.of(2026, 12, 25)))
        assertEquals(
            setOf(Holiday.WHIT_MONDAY, Holiday.MEMORIAL_DAY, Holiday.SPRING_BANK_HOLIDAY),
            Holiday.on(LocalDate.of(2026, 5, 25)).toSet()
        )
        assertTrue(Holiday.on(LocalDate.of(2026, 3, 3)).isEmpty())
        assertEquals(Holiday.HALLOWEEN, Holiday.getByID("HALLOWEEN"))
        assertNull(Holiday.getByID("not_a_holiday"))
        assertEquals(Holiday.entries.size, Holiday.inYear(2026).size)
    }

    @Test
    fun timezoneDecidesTheDay() {
        // 2026-12-25 03:00 UTC is still Christmas Eve in New York
        val instant = Instant.parse("2026-12-25T03:00:00Z")
        assertTrue(Holiday.CHRISTMAS_DAY in Holiday.on(instant, ZoneId.of("Europe/London")))
        assertTrue(Holiday.CHRISTMAS_EVE in Holiday.on(instant, ZoneId.of("America/New_York")))
    }
}
