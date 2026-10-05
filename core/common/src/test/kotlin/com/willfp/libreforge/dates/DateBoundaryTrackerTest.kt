package com.willfp.libreforge.dates

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DateBoundaryTrackerTest {
    private fun at(text: String): LocalDateTime = LocalDateTime.parse(text)

    private fun DateBoundaryTracker.names(now: String): List<String> =
        advance(at(now)).map { "${it.period.name.lowercase()}_${it.edge.name.lowercase()}" }

    @Test
    fun firstTickFiresNothing() {
        val tracker = DateBoundaryTracker()
        assertEquals(emptyList<String>(), tracker.names("2026-10-31T23:59:30"))
        assertEquals(emptyList<String>(), tracker.names("2026-10-31T23:59:31"), "Ends in progress at startup are skipped")
    }

    @Test
    fun hourStartAndEnd() {
        val tracker = DateBoundaryTracker()
        tracker.names("2026-10-01T14:58:59")
        assertEquals(listOf("hour_end"), tracker.names("2026-10-01T14:59:00"))
        assertEquals(emptyList<String>(), tracker.names("2026-10-01T14:59:01"), "No double fire")
        assertEquals(listOf("hour_start"), tracker.names("2026-10-01T15:00:00"))
        assertEquals(emptyList<String>(), tracker.names("2026-10-01T15:00:01"))
    }

    @Test
    fun dayEndsBeforeMidnightAndStartsAtMidnight() {
        val tracker = DateBoundaryTracker()
        tracker.names("2026-10-01T23:58:59")
        assertEquals(listOf("hour_end", "day_end"), tracker.names("2026-10-01T23:59:00"))
        assertEquals(listOf("hour_start", "day_start"), tracker.names("2026-10-02T00:00:00"))
    }

    @Test
    fun monthEndOnlyOnLastDay() {
        val tracker = DateBoundaryTracker()
        tracker.names("2026-10-30T23:58:59")
        assertEquals(listOf("hour_end", "day_end"), tracker.names("2026-10-30T23:59:00"))
        tracker.names("2026-10-31T23:58:59")
        assertEquals(listOf("hour_end", "day_end", "month_end"), tracker.names("2026-10-31T23:59:00"))
        assertEquals(listOf("hour_start", "day_start", "month_start"), tracker.names("2026-11-01T00:00:00"))
    }

    @Test
    fun stallAcrossBoundarySkipsTheMissedEnd() {
        val tracker = DateBoundaryTracker()
        tracker.names("2026-10-01T13:58:59")
        // A stalled clock can jump from before one hour's final minute straight into the next hour
        assertEquals(listOf("hour_start"), tracker.names("2026-10-01T14:00:05"))
        assertEquals(listOf("hour_end"), tracker.names("2026-10-01T14:59:00"))
    }

    @Test
    fun skippedFinalMinuteIsNotReplayed() {
        val tracker = DateBoundaryTracker()
        tracker.names("2026-10-01T23:58:00")
        assertEquals(listOf("hour_start", "day_start"), tracker.names("2026-10-02T00:00:30"))
    }

    @Test
    fun boundaryCarriesPeriodStart() {
        val tracker = DateBoundaryTracker()
        tracker.names("2026-10-31T23:58:59")
        val monthEnd = tracker.advance(at("2026-10-31T23:59:00")).single { it.period == DatePeriod.MONTH }
        assertEquals(at("2026-10-01T00:00:00"), monthEnd.periodStart)
        assertEquals(at("2026-10-31T23:59:00"), monthEnd.dateTime)
    }

    @Test
    fun clockGoingBackDoesNotRefire() {
        val tracker = DateBoundaryTracker()
        tracker.names("2026-10-25T01:58:59")
        assertEquals(listOf("hour_end"), tracker.names("2026-10-25T01:59:00"))
        // Daylight saving ends: 01:59 comes round again
        assertEquals(emptyList<String>(), tracker.names("2026-10-25T01:00:00"))
        assertEquals(emptyList<String>(), tracker.names("2026-10-25T01:59:00"))
    }
}
