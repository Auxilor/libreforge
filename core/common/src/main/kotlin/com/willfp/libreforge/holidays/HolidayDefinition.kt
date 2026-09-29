package com.willfp.libreforge.holidays

import java.time.LocalDate
import java.time.format.DateTimeParseException

/**
 * A holiday as written in holidays.yml, before it's been checked and compiled.
 */
data class HolidayDefinition(
    val id: String,
    val active: String? = null,
    val dates: List<String>? = null,
    val from: String? = null,
    val to: String? = null
)

/**
 * Turns [HolidayDefinition]s into [Holiday]s, reporting (and skipping) any that are invalid.
 */
class HolidayCompiler(
    private val evaluate: (String) -> Double?,
    private val warn: (String) -> Unit
) {
    private val idPattern = Regex("[a-z0-9_]{1,100}")

    fun compile(definitions: List<HolidayDefinition>): List<Holiday> {
        val byId = linkedMapOf<String, HolidayDefinition>()

        for (definition in definitions) {
            if (!idPattern.matches(definition.id)) {
                warn("Invalid holiday ID '${definition.id}': must be lowercase letters, numbers and underscores")
                continue
            }

            if (definition.id in byId) {
                warn("Duplicate holiday ID '${definition.id}', only the first is used")
                continue
            }

            byId[definition.id] = definition
        }

        val compiled = linkedMapOf<String, Holiday?>()
        val inProgress = mutableSetOf<String>()

        // Depth-first so ranges can hold their from/to holidays directly; inProgress catches cycles
        fun resolve(id: String): Holiday? {
            if (id in compiled) {
                return compiled[id]
            }

            val definition = byId[id] ?: return null

            if (!inProgress.add(id)) {
                warn("Holiday '$id' is part of a from/to cycle")
                compiled[id] = null
                return null
            }

            val rule = compileRule(definition, ::resolve)
            inProgress.remove(id)

            // A cycle may already have recorded this as invalid
            if (id in compiled) {
                return compiled[id]
            }

            val holiday = rule?.let { Holiday(id, it) }
            compiled[id] = holiday
            return holiday
        }

        return byId.keys.mapNotNull { resolve(it) }
    }

    private fun compileRule(definition: HolidayDefinition, resolve: (String) -> Holiday?): HolidayRule? {
        val id = definition.id
        val hasRange = definition.from != null || definition.to != null
        val kinds = listOfNotNull(
            "active".takeIf { definition.active != null },
            "dates".takeIf { definition.dates != null },
            "from/to".takeIf { hasRange }
        )

        if (kinds.size != 1) {
            warn("Holiday '$id' must set exactly one of active, dates or from/to (found: ${kinds.ifEmpty { listOf("none") }.joinToString()})")
            return null
        }

        return when {
            definition.active != null -> compileExpression(id, definition.active)
            definition.dates != null -> compileDates(id, definition.dates)
            else -> compileRange(id, definition.from, definition.to, resolve)
        }
    }

    private fun compileExpression(id: String, expression: String): HolidayRule? {
        val unknown = HolidayDateVariables.unknownIn(expression)
        if (unknown.isNotEmpty()) {
            warn(
                "Holiday '$id' uses unknown placeholders: ${unknown.joinToString { "%$it%" }}. " +
                        "Available: ${HolidayDateVariables.names.joinToString { "%$it%" }}"
            )
            return null
        }

        if (evaluate(HolidayDateVariables.substitute(expression, LocalDate.of(2000, 1, 1))) == null) {
            warn("Holiday '$id' has an invalid expression: $expression")
            return null
        }

        return HolidayRule.Expression(expression, evaluate)
    }

    private fun compileDates(id: String, entries: List<String>): HolidayRule? {
        val dates = mutableSetOf<LocalDate>()

        for (entry in entries) {
            val parts = entry.split("..").map { it.trim() }
            try {
                val start = LocalDate.parse(parts[0])
                val end = if (parts.size == 2) LocalDate.parse(parts[1]) else start

                if (parts.size > 2 || end.isBefore(start)) {
                    warn("Holiday '$id' has an invalid date range: $entry")
                    return null
                }

                start.datesUntil(end.plusDays(1)).forEach { dates += it }
            } catch (e: DateTimeParseException) {
                warn("Holiday '$id' has an invalid date: $entry (expected YYYY-MM-DD or YYYY-MM-DD..YYYY-MM-DD)")
                return null
            }
        }

        return HolidayRule.Dates(dates)
    }

    private fun compileRange(id: String, fromId: String?, toId: String?, resolve: (String) -> Holiday?): HolidayRule? {
        if (fromId == null || toId == null) {
            warn("Holiday '$id' must set both from and to")
            return null
        }

        val from = resolve(fromId)
        val to = resolve(toId)

        if (from == null || to == null) {
            val missing = listOfNotNull(fromId.takeIf { from == null }, toId.takeIf { to == null })
            warn("Holiday '$id' refers to missing or invalid holidays: ${missing.joinToString()}")
            return null
        }

        return HolidayRule.Range(from, to)
    }
}
