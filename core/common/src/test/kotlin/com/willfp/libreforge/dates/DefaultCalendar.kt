package com.willfp.libreforge.dates

import org.yaml.snakeyaml.Yaml

/**
 * The entries from a bundled calendar file (e.g. holidays.yml), compiled with [TestExpressionEvaluator].
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
            DateDefinition(
                id = entry["id"] as String,
                active = entry["active"] as String?,
                dates = (entry["dates"] as List<*>?)?.map { it.toString() },
                from = entry["from"] as String?,
                to = entry["to"] as String?
            )
        }

        DateCompiler(kind, create, TestExpressionEvaluator) { warnings += it }
            .compile(definitions)
            .associateBy { it.id }
    }

    operator fun get(id: String): T = requireNotNull(entries[id]) { "No default $kind '$id'" }
}
