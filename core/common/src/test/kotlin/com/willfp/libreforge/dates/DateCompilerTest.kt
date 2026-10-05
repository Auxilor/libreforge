package com.willfp.libreforge.dates

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DateCompilerTest {
    private class TestEntry(id: String, rule: DateRule) : DateEntry(id, rule)

    private val warnings = mutableListOf<String>()

    private fun compile(vararg definitions: DateDefinition): Map<String, TestEntry> =
        DateCompiler("holiday", ::TestEntry) { warnings += it }
            .compile(definitions.toList())
            .associateBy { it.id }

    @Test
    fun explicitDatesAndRanges() {
        val diwali = compile(
            DateDefinition("diwali", dates = listOf("2026-11-08", "2027-10-29..2027-10-31"))
        ).getValue("diwali")

        assertTrue(diwali.isOn(LocalDate.of(2026, 11, 8)))
        assertTrue(diwali.startsOn(LocalDate.of(2027, 10, 29)))
        assertTrue(diwali.isOn(LocalDate.of(2027, 10, 31)))
        assertFalse(diwali.startsOn(LocalDate.of(2027, 10, 30)))
        assertFalse(diwali.isOn(LocalDate.of(2027, 11, 1)))
        assertEquals(emptyList<String>(), warnings)
    }

    @Test
    fun rangeCrossesNewYear() {
        val holidays = compile(
            DateDefinition("winter_break", dates = listOf("%winter_start%..%winter_end%")),
            DateDefinition("winter_start", dates = listOf("12-20")),
            DateDefinition("winter_end", dates = listOf("01-03")),
            DateDefinition("annual_winter_break", dates = listOf("12-20..01-03")),
            DateDefinition("mixed_winter_break", dates = listOf("%winter_start%..01-03"))
        )

        for (id in listOf("winter_break", "annual_winter_break", "mixed_winter_break")) {
            val winterBreak = holidays.getValue(id)
            assertTrue(winterBreak.startsOn(LocalDate.of(2026, 12, 20)), id)
            assertTrue(winterBreak.isOn(LocalDate.of(2027, 1, 3)), id)
            assertFalse(winterBreak.isOn(LocalDate.of(2027, 1, 4)), id)
            assertFalse(winterBreak.isOn(LocalDate.of(2026, 12, 19)), id)
        }

        assertEquals(emptyList<String>(), warnings)
    }

    @Test
    fun rulesMustAllMatch() {
        val thanksgiving = compile(
            DateDefinition("thanksgiving", months = listOf("november"), daysOfWeek = listOf("thursday"), weeks = listOf("4"))
        ).getValue("thanksgiving")

        // November 2029 has five Thursdays; the fourth is the 22nd
        assertTrue(thanksgiving.isOn(LocalDate.of(2029, 11, 22)))
        assertFalse(thanksgiving.isOn(LocalDate.of(2029, 11, 29)))
        assertFalse(thanksgiving.isOn(LocalDate.of(2029, 11, 23)))
        assertFalse(thanksgiving.isOn(LocalDate.of(2029, 10, 25)))
        assertEquals(emptyList<String>(), warnings)
    }

    @Test
    fun lastWeekOfMonth() {
        val memorialDay = compile(
            DateDefinition("memorial_day", months = listOf("may"), daysOfWeek = listOf("monday"), weeks = listOf("last"))
        ).getValue("memorial_day")

        assertTrue(memorialDay.isOn(LocalDate.of(2026, 5, 25)))
        assertTrue(memorialDay.isOn(LocalDate.of(2027, 5, 31)))
        assertFalse(memorialDay.isOn(LocalDate.of(2027, 5, 24)))
        assertEquals(emptyList<String>(), warnings)
    }

    @Test
    fun offsetsShiftReferencesAndBuiltIns() {
        val holidays = compile(
            DateDefinition("good_friday", dates = listOf("%easter%"), dateOffset = "-2"),
            DateDefinition("christmas_period", dates = listOf("12-24..12-26")),
            DateDefinition("after_christmas", dates = listOf("%christmas_period%"), dateOffset = "7")
        )

        assertTrue(holidays.getValue("good_friday").isOn(LocalDate.of(2026, 4, 3)))
        assertFalse(holidays.getValue("good_friday").isOn(LocalDate.of(2026, 4, 5)))

        val afterChristmas = holidays.getValue("after_christmas")
        assertTrue(afterChristmas.startsOn(LocalDate.of(2026, 12, 31)))
        assertTrue(afterChristmas.endsOn(LocalDate.of(2027, 1, 2)))
        assertFalse(afterChristmas.isOn(LocalDate.of(2026, 12, 26)))
        assertEquals(emptyList<String>(), warnings)
    }

    @Test
    fun offsetRanges() {
        val holidays = compile(
            DateDefinition("christmas_day", dates = listOf("12-25")),
            DateDefinition("twelve_days_of_christmas", dates = listOf("%christmas_day%"), dateOffset = "0..11"),
            DateDefinition("holy_week", dates = listOf("%easter%"), dateOffset = "-7..-1")
        )

        val twelveDays = holidays.getValue("twelve_days_of_christmas")
        assertTrue(twelveDays.startsOn(LocalDate.of(2026, 12, 25)))
        assertTrue(twelveDays.endsOn(LocalDate.of(2027, 1, 5)))
        assertEquals(12, LocalDate.of(2026, 12, 1).datesUntil(LocalDate.of(2027, 2, 1)).filter { twelveDays.isOn(it) }.count())

        val holyWeek = holidays.getValue("holy_week")
        assertTrue(holyWeek.startsOn(LocalDate.of(2026, 3, 29)))
        assertTrue(holyWeek.endsOn(LocalDate.of(2026, 4, 4)))
        assertEquals(emptyList<String>(), warnings)
    }

    @Test
    fun invalidDefinitionsAreSkipped() {
        val holidays = compile(
            DateDefinition("none"),
            DateDefinition("offset_only", dateOffset = "1"),
            DateDefinition("bad_offset", dates = listOf("01-01"), dateOffset = "one"),
            DateDefinition("backwards_offset", dates = listOf("01-01"), dateOffset = "5..1"),
            DateDefinition("bad_date", dates = listOf("2026-13-01")),
            DateDefinition("backwards", dates = listOf("2026-02-01..2026-01-01")),
            DateDefinition("mixed_formats", dates = listOf("2026-01-01..02-01")),
            DateDefinition("three_sides", dates = listOf("01-01..02-01..03-01")),
            DateDefinition("bad_month", months = listOf("smarch")),
            DateDefinition("bad_week", weeks = listOf("6")),
            DateDefinition("missing_ref", dates = listOf("%nope%")),
            DateDefinition("cycle_a", dates = listOf("%cycle_b%")),
            DateDefinition("cycle_b", dates = listOf("%cycle_a%..%valid%")),
            DateDefinition("easter", dates = listOf("04-01")),
            DateDefinition("Bad-ID", dates = listOf("01-01")),
            DateDefinition("valid", dates = listOf("03-01")),
            DateDefinition("valid", dates = listOf("03-02"))
        )

        assertEquals(setOf("valid"), holidays.keys)
        assertTrue(holidays.getValue("valid").isOn(LocalDate.of(2026, 3, 1)), "First definition of a duplicate wins")
        assertTrue(warnings.any { "Duplicate" in it })
        assertTrue(warnings.any { "cycle" in it })
        assertTrue(warnings.any { "%nope%" in it })
        assertTrue(warnings.any { "built-in" in it })
        assertTrue(warnings.any { "smarch" in it })
        assertTrue(warnings.any { "'one'" in it })
        assertTrue(warnings.any { "ends before it starts" in it })
    }
}
