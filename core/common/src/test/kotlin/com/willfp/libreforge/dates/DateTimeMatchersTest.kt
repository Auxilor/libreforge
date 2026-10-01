package com.willfp.libreforge.dates

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DateTimeMatchersTest {
    private fun at(date: String, time: String = "12:00"): LocalDateTime =
        LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(time))

    private fun rejects(block: () -> Unit): String =
        assertThrows<IllegalArgumentException> { block() }.message ?: ""

    @Test
    fun monthsByNameOrNumber() {
        val matcher = DateTimeMatchers.months(listOf("December", "1"))
        assertTrue(matcher.matches(at("2026-12-25")))
        assertTrue(matcher.matches(at("2027-01-31")))
        assertFalse(matcher.matches(at("2026-11-30")))
    }

    @Test
    fun monthsRejectInvalid() {
        rejects { DateTimeMatchers.months(listOf("13")) }
        rejects { DateTimeMatchers.months(listOf("decembuary")) }
        rejects { DateTimeMatchers.months(emptyList()) }
    }

    @Test
    fun daysOfWeekByNameOrIsoNumber() {
        val matcher = DateTimeMatchers.daysOfWeek(listOf("FRIDAY", "7"))
        assertTrue(matcher.matches(at("2026-10-02")), "Friday")
        assertTrue(matcher.matches(at("2026-10-04")), "Sunday is 7")
        assertFalse(matcher.matches(at("2026-10-05")), "Monday")
        rejects { DateTimeMatchers.daysOfWeek(listOf("0")) }
        rejects { DateTimeMatchers.daysOfWeek(listOf("fri")) }
    }

    @Test
    fun daysOfMonth() {
        val matcher = DateTimeMatchers.daysOfMonth(listOf("1", "31"))
        assertTrue(matcher.matches(at("2026-10-01")))
        assertTrue(matcher.matches(at("2026-10-31")))
        assertFalse(matcher.matches(at("2026-09-30")))
        rejects { DateTimeMatchers.daysOfMonth(listOf("32")) }
        rejects { DateTimeMatchers.daysOfMonth(listOf("0")) }
    }

    @Test
    fun annualAndExactDates() {
        val matcher = DateTimeMatchers.dates(listOf("12-25", "2026-10-31"))
        assertTrue(matcher.matches(at("2026-12-25")))
        assertTrue(matcher.matches(at("2031-12-25")))
        assertTrue(matcher.matches(at("2026-10-31")))
        assertFalse(matcher.matches(at("2027-10-31")), "Exact dates don't recur")
        rejects { DateTimeMatchers.dates(listOf("13-01")) }
        rejects { DateTimeMatchers.dates(listOf("2026-02-30")) }
        rejects { DateTimeMatchers.dates(listOf("christmas")) }
    }

    @Test
    fun leapDayOnlyInLeapYears() {
        val matcher = DateTimeMatchers.dates(listOf("02-29"))
        assertTrue(matcher.matches(at("2028-02-29")))
        assertFalse(matcher.matches(at("2027-02-28")))
        assertFalse(matcher.matches(at("2027-03-01")))
    }

    @Test
    fun annualDateRangeWithinYear() {
        val matcher = DateTimeMatchers.dateBetween("06-01", "08-31")
        assertTrue(matcher.matches(at("2026-06-01")))
        assertTrue(matcher.matches(at("2026-08-31")))
        assertFalse(matcher.matches(at("2026-09-01")))
        assertFalse(matcher.matches(at("2026-05-31")))
    }

    @Test
    fun annualDateRangeWrapsNewYear() {
        val matcher = DateTimeMatchers.dateBetween("12-20", "01-05")
        assertTrue(matcher.matches(at("2026-12-20")))
        assertTrue(matcher.matches(at("2026-12-31")))
        assertTrue(matcher.matches(at("2027-01-05")))
        assertFalse(matcher.matches(at("2027-01-06")))
        assertFalse(matcher.matches(at("2026-12-19")))
    }

    @Test
    fun exactDateRange() {
        val matcher = DateTimeMatchers.dateBetween("2026-12-20", "2027-01-05")
        assertTrue(matcher.matches(at("2027-01-01")))
        assertFalse(matcher.matches(at("2027-12-25")))
        rejects { DateTimeMatchers.dateBetween("2027-01-05", "2026-12-20") }
    }

    @Test
    fun dateRangeRejectsMixedFormats() {
        val message = rejects { DateTimeMatchers.dateBetween("12-20", "2027-01-05") }
        assertTrue(message.contains("same format"), message)
    }

    @Test
    fun hours() {
        val matcher = DateTimeMatchers.hours(listOf("0", "18"))
        assertTrue(matcher.matches(at("2026-10-01", "00:30")))
        assertTrue(matcher.matches(at("2026-10-01", "18:59")))
        assertFalse(matcher.matches(at("2026-10-01", "19:00")))
        rejects { DateTimeMatchers.hours(listOf("24")) }
    }

    @Test
    fun hourRangeWithinDayAndWrapping() {
        val daytime = DateTimeMatchers.hourBetween("9", "17")
        assertTrue(daytime.matches(at("2026-10-01", "09:00")))
        assertTrue(daytime.matches(at("2026-10-01", "17:59")))
        assertFalse(daytime.matches(at("2026-10-01", "18:00")))

        val overnight = DateTimeMatchers.hourBetween("22", "4")
        assertTrue(overnight.matches(at("2026-10-01", "23:15")))
        assertTrue(overnight.matches(at("2026-10-01", "04:59")))
        assertFalse(overnight.matches(at("2026-10-01", "05:00")))
        assertFalse(overnight.matches(at("2026-10-01", "21:59")))
    }

    @Test
    fun timeRangeIncludesWholeEndMinute() {
        val matcher = DateTimeMatchers.timeBetween("09:30", "10:15")
        assertTrue(matcher.matches(at("2026-10-01", "09:30")))
        assertTrue(matcher.matches(LocalDateTime.of(2026, 10, 1, 10, 15, 59)))
        assertFalse(matcher.matches(at("2026-10-01", "10:16")))
        assertFalse(matcher.matches(LocalDateTime.of(2026, 10, 1, 9, 29, 59)))
    }

    @Test
    fun timeRangeWrapsMidnight() {
        val matcher = DateTimeMatchers.timeBetween("21:30", "6:00")
        assertTrue(matcher.matches(at("2026-10-01", "23:59")))
        assertTrue(matcher.matches(at("2026-10-01", "00:00")))
        assertTrue(matcher.matches(at("2026-10-01", "06:00")))
        assertFalse(matcher.matches(at("2026-10-01", "06:01")))
        assertFalse(matcher.matches(at("2026-10-01", "21:29")))
        rejects { DateTimeMatchers.timeBetween("25:00", "06:00") }
        rejects { DateTimeMatchers.timeBetween("9", "10") }
    }

    @Test
    fun errorMessagesNameTheValue() {
        assertEquals(true, rejects { DateTimeMatchers.months(listOf("smarch")) }.contains("smarch"))
    }
}
