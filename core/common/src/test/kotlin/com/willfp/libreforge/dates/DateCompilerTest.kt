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
        DateCompiler("holiday", ::TestEntry, TestExpressionEvaluator) { warnings += it }
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
            DateDefinition("winter_break", from = "winter_start", to = "winter_end"),
            DateDefinition("winter_start", active = "%month% == 12 && %day% == 20"),
            DateDefinition("winter_end", active = "%month% == 1 && %day% == 3")
        )

        val winterBreak = holidays.getValue("winter_break")
        assertTrue(winterBreak.startsOn(LocalDate.of(2026, 12, 20)))
        assertTrue(winterBreak.isOn(LocalDate.of(2027, 1, 3)))
        assertFalse(winterBreak.isOn(LocalDate.of(2027, 1, 4)))
        assertFalse(winterBreak.isOn(LocalDate.of(2026, 12, 19)))
        assertEquals(emptyList<String>(), warnings)
    }

    @Test
    fun invalidDefinitionsAreSkipped() {
        val holidays = compile(
            DateDefinition("none"),
            DateDefinition("both", active = "%day% == 1", dates = listOf("2026-01-01")),
            DateDefinition("bad_date", dates = listOf("2026-13-01")),
            DateDefinition("backwards", dates = listOf("2026-02-01..2026-01-01")),
            DateDefinition("unknown_placeholder", active = "%moon_phase% == 1"),
            DateDefinition("bad_expression", active = "%day% == == 1"),
            DateDefinition("half_range", from = "valid"),
            DateDefinition("missing_ref", from = "valid", to = "nope"),
            DateDefinition("cycle_a", from = "cycle_b", to = "valid"),
            DateDefinition("cycle_b", from = "cycle_a", to = "valid"),
            DateDefinition("Bad-ID", active = "%day% == 1"),
            DateDefinition("valid", active = "%day% == 1"),
            DateDefinition("valid", active = "%day% == 2")
        )

        assertEquals(setOf("valid"), holidays.keys)
        assertTrue(holidays.getValue("valid").isOn(LocalDate.of(2026, 3, 1)), "First definition of a duplicate wins")
        assertTrue(warnings.any { "Duplicate" in it })
        assertTrue(warnings.any { "cycle" in it })
        assertTrue(warnings.any { "%moon_phase%" in it })
    }

    @Test
    fun placeholdersSubstituteWithBrackets() {
        // 2026-04-03 is Good Friday, two days before Easter
        assertEquals(
            "(3) == 3 && (-2) == -2",
            DateVariables.substitute("%day% == 3 && %days_from_easter% == -2", LocalDate.of(2026, 4, 3))
        )
        assertEquals(setOf("nope"), DateVariables.unknownIn("%year% % 4 == 0 && %nope% == 1"))
    }
}
