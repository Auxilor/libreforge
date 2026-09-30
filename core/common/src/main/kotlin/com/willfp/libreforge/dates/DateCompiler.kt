package com.willfp.libreforge.dates

/**
 * A holiday or season as written in its config file, before it's been checked and compiled.
 *
 * Every rule that is set must match; each list matches if any of its values do.
 */
data class DateDefinition(
    val id: String,
    val dates: List<String>? = null,
    val months: List<String>? = null,
    val daysOfWeek: List<String>? = null,
    val weeks: List<String>? = null,
    val dateOffset: String? = null
)

/**
 * Turns [DateDefinition]s into [DateEntry]s made by [create], reporting (and skipping) any that are invalid.
 *
 * [kind] names the entries in warnings, e.g. `holiday`.
 */
class DateCompiler<T : DateEntry>(
    private val kind: String,
    private val create: (id: String, rule: DateRule) -> T,
    private val warn: (String) -> Unit
) {
    private val idPattern = Regex("[a-z0-9_]{1,100}")

    private val referencePattern = Regex("%([a-z0-9_]+)%")

    private val kindTitle = kind.replaceFirstChar { it.uppercase() }

    fun compile(definitions: List<DateDefinition>): List<T> {
        val byId = linkedMapOf<String, DateDefinition>()

        for (definition in definitions) {
            if (!idPattern.matches(definition.id)) {
                warn("Invalid $kind ID '${definition.id}': must be lowercase letters, numbers and underscores")
                continue
            }

            if (definition.id in builtInDates) {
                warn("Invalid $kind ID '${definition.id}': %${definition.id}% is a built-in date")
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

        // Depth-first so references can hold the entries they refer to directly; inProgress catches cycles
        fun resolve(id: String): T? {
            if (id in compiled) {
                return compiled[id]
            }

            val definition = byId[id] ?: return null

            if (!inProgress.add(id)) {
                warn("$kindTitle '$id' is part of a reference cycle")
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
        return try {
            val rules = listOfNotNull(
                definition.dates?.let { dates -> DateRule.AnyOf(dates.map { compileDate(it, resolve) }) },
                definition.months?.let { DateRule.Matching(DateTimeMatchers.months(it)) },
                definition.daysOfWeek?.let { DateRule.Matching(DateTimeMatchers.daysOfWeek(it)) },
                definition.weeks?.let { DateRule.Matching(DateTimeMatchers.weeksOfMonth(it)) }
            )

            require(rules.isNotEmpty()) { "Must set at least one of date, month, day_of_week or week" }

            val rule = rules.singleOrNull() ?: DateRule.AllOf(rules)
            definition.dateOffset?.let { compileOffset(rule, it) } ?: rule
        } catch (e: IllegalArgumentException) {
            warn("$kindTitle '${definition.id}': ${e.message}")
            null
        }
    }

    private fun compileOffset(rule: DateRule, value: String): DateRule {
        val sides = value.split("..").map { side ->
            side.trim().toLongOrNull()
                ?: throw IllegalArgumentException("Invalid date_offset '$value': must be a number of days, or a range like 0..11")
        }

        require(sides.size <= 2) { "Invalid date_offset '$value': must be a number of days, or a range like 0..11" }

        val (start, end) = sides.first() to sides.last()
        require(end >= start) { "Invalid date_offset '$value': range ends before it starts" }

        return if (start == end) {
            DateRule.Offset(rule, start)
        } else {
            DateRule.AnyOf((start..end).map { DateRule.Offset(rule, it) })
        }
    }

    private fun compileDate(value: String, resolve: (String) -> T?): DateRule {
        val sides = value.split("..").map { it.trim() }

        return when {
            sides.size == 1 -> compileDateSide(sides[0], resolve)

            sides.size > 2 -> throw IllegalArgumentException("Invalid date range '$value': must be two dates separated by ..")

            sides.none { referencePattern.matches(it) } ->
                DateRule.Matching(DateTimeMatchers.dateBetween(sides[0], sides[1]))

            else -> DateRule.Range(compileDateSide(sides[0], resolve), compileDateSide(sides[1], resolve))
        }
    }

    private fun compileDateSide(value: String, resolve: (String) -> T?): DateRule {
        val id = referencePattern.matchEntire(value)?.groupValues?.get(1)
            ?: return DateRule.Matching(DateTimeMatchers.dates(listOf(value)))

        builtInDates[id]?.let { return it }

        val entry = resolve(id) ?: throw IllegalArgumentException("Refers to missing or invalid $kind %$id%")
        return DateRule.Reference(entry)
    }

    private companion object {
        val builtInDates: Map<String, DateRule> = mapOf(
            "easter" to DateRule { it == Dates.easterSunday(it.year) }
        )
    }
}
