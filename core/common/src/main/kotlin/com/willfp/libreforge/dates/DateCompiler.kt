package com.willfp.libreforge.dates

import java.time.LocalDate
import java.time.format.DateTimeParseException

/**
 * A holiday or season as written in its config file, before it's been checked and compiled.
 */
data class DateDefinition(
    val id: String,
    val active: String? = null,
    val dates: List<String>? = null,
    val from: String? = null,
    val to: String? = null
)

/**
 * Turns [DateDefinition]s into [DateEntry]s made by [create], reporting (and skipping) any that are invalid.
 *
 * [kind] names the entries in warnings, e.g. `holiday`.
 */
class DateCompiler<T : DateEntry>(
    private val kind: String,
    private val create: (id: String, rule: DateRule) -> T,
    private val evaluate: (String) -> Double?,
    private val warn: (String) -> Unit
) {
    private val idPattern = Regex("[a-z0-9_]{1,100}")

    private val kindTitle = kind.replaceFirstChar { it.uppercase() }

    fun compile(definitions: List<DateDefinition>): List<T> {
        val byId = linkedMapOf<String, DateDefinition>()

        for (definition in definitions) {
            if (!idPattern.matches(definition.id)) {
                warn("Invalid $kind ID '${definition.id}': must be lowercase letters, numbers and underscores")
                continue
            }

            if (definition.id in byId) {
                warn("Duplicate $kind ID '${definition.id}', only the first is used")
                continue
            }

            byId[definition.id] = definition
        }

        val compiled = linkedMapOf<String, T?>()
        val inProgress = mutableSetOf<String>()

        // Depth-first so ranges can hold their from/to entries directly; inProgress catches cycles
        fun resolve(id: String): T? {
            if (id in compiled) {
                return compiled[id]
            }

            val definition = byId[id] ?: return null

            if (!inProgress.add(id)) {
                warn("$kindTitle '$id' is part of a from/to cycle")
                compiled[id] = null
                return null
            }

            val rule = compileRule(definition, ::resolve)
            inProgress.remove(id)

            // A cycle may already have recorded this as invalid
            if (id in compiled) {
                return compiled[id]
            }

            val entry = rule?.let { create(id, it) }
            compiled[id] = entry
            return entry
        }

        return byId.keys.mapNotNull { resolve(it) }
    }

    private fun compileRule(definition: DateDefinition, resolve: (String) -> T?): DateRule? {
        val id = definition.id
        val hasRange = definition.from != null || definition.to != null
        val kinds = listOfNotNull(
            "active".takeIf { definition.active != null },
            "dates".takeIf { definition.dates != null },
            "from/to".takeIf { hasRange }
        )

        if (kinds.size != 1) {
            warn("$kindTitle '$id' must set exactly one of active, dates or from/to (found: ${kinds.ifEmpty { listOf("none") }.joinToString()})")
            return null
        }

        return when {
            definition.active != null -> compileExpression(id, definition.active)
            definition.dates != null -> compileDates(id, definition.dates)
            else -> compileRange(id, definition.from, definition.to, resolve)
        }
    }

    private fun compileExpression(id: String, expression: String): DateRule? {
        val unknown = DateVariables.unknownIn(expression)
        if (unknown.isNotEmpty()) {
            warn(
                "$kindTitle '$id' uses unknown placeholders: ${unknown.joinToString { "%$it%" }}. " +
                        "Available: ${DateVariables.names.joinToString { "%$it%" }}"
            )
            return null
        }

        if (evaluate(DateVariables.substitute(expression, LocalDate.of(2000, 1, 1))) == null) {
            warn("$kindTitle '$id' has an invalid expression: $expression")
            return null
        }

        return DateRule.Expression(expression, evaluate)
    }

    private fun compileDates(id: String, entries: List<String>): DateRule? {
        val dates = mutableSetOf<LocalDate>()

        for (entry in entries) {
            val parts = entry.split("..").map { it.trim() }
            try {
                val start = LocalDate.parse(parts[0])
                val end = if (parts.size == 2) LocalDate.parse(parts[1]) else start

                if (parts.size > 2 || end.isBefore(start)) {
                    warn("$kindTitle '$id' has an invalid date range: $entry")
                    return null
                }

                start.datesUntil(end.plusDays(1)).forEach { dates += it }
            } catch (e: DateTimeParseException) {
                warn("$kindTitle '$id' has an invalid date: $entry (expected YYYY-MM-DD or YYYY-MM-DD..YYYY-MM-DD)")
                return null
            }
        }

        return DateRule.Explicit(dates)
    }

    private fun compileRange(id: String, fromId: String?, toId: String?, resolve: (String) -> T?): DateRule? {
        if (fromId == null || toId == null) {
            warn("$kindTitle '$id' must set both from and to")
            return null
        }

        val from = resolve(fromId)
        val to = resolve(toId)

        if (from == null || to == null) {
            val missing = listOfNotNull(fromId.takeIf { from == null }, toId.takeIf { to == null })
            warn("$kindTitle '$id' refers to missing or invalid ${kind}s: ${missing.joinToString()}")
            return null
        }

        return DateRule.Range(from, to)
    }
}
