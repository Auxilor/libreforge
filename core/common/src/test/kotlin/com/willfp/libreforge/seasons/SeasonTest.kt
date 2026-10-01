package com.willfp.libreforge.seasons

import com.willfp.libreforge.dates.DateCompiler
import com.willfp.libreforge.dates.DateDefinition
import com.willfp.libreforge.dates.DefaultCalendar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SeasonTest {
    private object DefaultSeasons : DefaultCalendar<Season>("seasons", "season", ::Season)

    @Test
    fun defaultFileLoadsCleanly() {
        assertEquals(emptyList<String>(), DefaultSeasons.warnings)
        assertEquals(setOf("spring", "summer", "autumn", "winter"), DefaultSeasons.entries.keys)
    }

    @Test
    fun northernMeteorologicalSeasons() {
        assertEquals(listOf("winter"), seasonsOn(DefaultSeasons.entries.values, LocalDate.of(2026, 1, 15)))
        assertEquals(listOf("spring"), seasonsOn(DefaultSeasons.entries.values, LocalDate.of(2026, 3, 1)))
        assertEquals(listOf("summer"), seasonsOn(DefaultSeasons.entries.values, LocalDate.of(2026, 8, 31)))
        assertEquals(listOf("autumn"), seasonsOn(DefaultSeasons.entries.values, LocalDate.of(2026, 9, 29)))
        assertEquals(listOf("winter"), seasonsOn(DefaultSeasons.entries.values, LocalDate.of(2026, 12, 1)))
    }

    @Test
    fun startsOnFirstDay() {
        val winter = DefaultSeasons["winter"]
        assertTrue(winter.startsOn(LocalDate.of(2026, 12, 1)))
        assertFalse(winter.startsOn(LocalDate.of(2026, 12, 2)))
        assertFalse(winter.startsOn(LocalDate.of(2027, 1, 1)), "Winter carries on across New Year")
        assertTrue(winter.endsOn(LocalDate.of(2028, 2, 29)))
    }

    @Test
    fun everyDateHasExactlyOneSeason() {
        val southern = DateCompiler("season", ::Season) { throw AssertionError(it) }
            .compile(southernDefinitions)

        for (seasons in listOf(DefaultSeasons.entries.values, southern)) {
            for (date in LocalDate.of(2026, 1, 1).datesUntil(LocalDate.of(2029, 1, 1))) {
                assertEquals(1, seasonsOn(seasons, date).size, "$date")
            }
        }
    }

    @Test
    fun southernSeasonsAreOffsetBySixMonths() {
        val southern = DateCompiler("season", ::Season) { throw AssertionError(it) }
            .compile(southernDefinitions)

        for (date in LocalDate.of(2026, 1, 1).datesUntil(LocalDate.of(2027, 1, 1))) {
            val northern = seasonsOn(DefaultSeasons.entries.values, date.plusMonths(6)).single()
            assertEquals(northern, seasonsOn(southern, date).single(), "$date")
        }
    }

    private companion object {
        fun seasonsOn(seasons: Collection<Season>, date: LocalDate): List<String> =
            seasons.filter { it.isOn(date) }.map { it.id }

        // Mirrors the commented southern hemisphere example in seasons.yml
        val southernDefinitions = listOf(
            DateDefinition("spring", months = listOf("september", "october", "november")),
            DateDefinition("summer", months = listOf("december", "january", "february")),
            DateDefinition("autumn", months = listOf("march", "april", "may")),
            DateDefinition("winter", months = listOf("june", "july", "august"))
        )
    }
}
