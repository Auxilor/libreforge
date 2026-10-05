package com.willfp.libreforge.dates

import org.yaml.snakeyaml.Yaml

/**
 * The entries from a bundled calendar file (e.g. holidays.yml), compiled as the plugin would.
 */
open class DefaultCalendar<T : DateEntry>(
    private val fileName: String,
    private val kind: String,
    private val create: (id: String, rule: DateRule) -> T
) {
    val warnings = mutableListOf<String>()

    val entries: Map<String, T> by lazy {
        val stream = requireNotNull(javaClass.classLoader.getResourceAsStream("$fileName.yml")) { "$fileName.yml missing" }
        val root: Map<String, Any?> = stream.use { Yaml().load(it) }

        @Suppress("UNCHECKED_CAST")
        val entries = root[fileName] as List<Map<String, Any?>>

        val definitions = entries.map { entry ->
            fun stringOrStrings(vararg keys: String): List<String>? =
                keys.firstNotNullOfOrNull { entry[it] }?.let { value ->
                    (value as? List<*>)?.map { it.toString() } ?: listOf(value.toString())
                }

            DateDefinition(
                id = entry["id"] as String,
                dates = stringOrStrings("date", "dates"),
                months = stringOrStrings("month", "months"),
                daysOfWeek = stringOrStrings("day_of_week", "days_of_week"),
                weeks = stringOrStrings("week", "weeks"),
                dateOffset = entry["date_offset"]?.toString()
            )
        }

        DateCompiler(kind, create) { warnings += it }
            .compile(definitions)
            .associateBy { it.id }
    }

    operator fun get(id: String): T = requireNotNull(entries[id]) { "No default $kind '$id'" }
}
