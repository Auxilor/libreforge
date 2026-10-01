package com.willfp.libreforge.holidays

import org.yaml.snakeyaml.Yaml

/**
 * The holidays from the bundled holidays.yml, compiled with [TestExpressionEvaluator].
 */
object DefaultHolidays {
    val warnings = mutableListOf<String>()

    val holidays: Map<String, Holiday> by lazy {
        val stream = requireNotNull(javaClass.classLoader.getResourceAsStream("holidays.yml")) { "holidays.yml missing" }
        val root: Map<String, Any?> = stream.use { Yaml().load(it) }

        @Suppress("UNCHECKED_CAST")
        val entries = root["holidays"] as List<Map<String, Any?>>

        val definitions = entries.map { entry ->
            HolidayDefinition(
                id = entry["id"] as String,
                active = entry["active"] as String?,
                dates = (entry["dates"] as List<*>?)?.map { it.toString() },
                from = entry["from"] as String?,
                to = entry["to"] as String?
            )
        }

        HolidayCompiler(TestExpressionEvaluator) { warnings += it }
            .compile(definitions)
            .associateBy { it.id }
    }

    operator fun get(id: String): Holiday = requireNotNull(holidays[id]) { "No default holiday '$id'" }
}
