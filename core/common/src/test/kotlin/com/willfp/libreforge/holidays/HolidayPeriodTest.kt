package com.willfp.libreforge.holidays

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class HolidayPeriodTest {
    @Test
    fun periods() {
        assertEquals(LocalDate.of(2026, 11, 29), HolidayPeriod.ADVENT.startIn(2026))
        // Christmas Eve on a Sunday: that Sunday is the fourth of Advent
        assertEquals(LocalDate.of(2023, 12, 3), HolidayPeriod.ADVENT.startIn(2023))
        assertTrue(HolidayPeriod.ADVENT.isOn(LocalDate.of(2026, 12, 24)))
        assertTrue(!HolidayPeriod.ADVENT.isOn(LocalDate.of(2026, 12, 25)))

        // Spans New Year: 3 Jan 2027 belongs to the period starting in 2026
        assertTrue(HolidayPeriod.TWELVE_DAYS_OF_CHRISTMAS.isOn(LocalDate.of(2027, 1, 3)))
        assertTrue(HolidayPeriod.TWELVE_DAYS_OF_CHRISTMAS.isOn(LocalDate.of(2026, 12, 25)))
        assertTrue(!HolidayPeriod.TWELVE_DAYS_OF_CHRISTMAS.isOn(LocalDate.of(2027, 1, 6)))

        assertEquals(
            setOf(HolidayPeriod.LENT, HolidayPeriod.HOLY_WEEK),
            HolidayPeriod.on(LocalDate.of(2026, 4, 1)).toSet()
        )
        assertEquals(listOf(HolidayPeriod.EASTERTIDE), HolidayPeriod.on(LocalDate.of(2026, 5, 24)))
        assertEquals(HolidayPeriod.ADVENT, HolidayPeriod.getByID("advent"))
    }
}
