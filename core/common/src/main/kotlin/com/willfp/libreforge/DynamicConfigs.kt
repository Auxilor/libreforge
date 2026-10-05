package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config

/**
 * Decides whether a config can change value while its holder stays the same.
 */
internal object DynamicConfigs {
    // Matched exactly as eco's findPlaceholders.
    private val PLACEHOLDER = Regex("%[^% ]+%")

    /**
     * If any string in [config] contains a placeholder other than [ownPlaceholders], or `rand`
     * outside placeholders (eco's only impure built-ins are `rand` and `random`).
     */
    fun isDynamic(config: Config, ownPlaceholders: Set<String>): Boolean =
        anyString(config) { isDynamic(it, ownPlaceholders) }

    /**
     * If [value] (a config, list, or string) is dynamic by the same rule, with no own placeholders.
     */
    fun isDynamicValue(value: Any?): Boolean =
        anyString(value) { isDynamic(it, emptySet()) }

    /**
     * If any string in [config] references a placeholder.
     */
    fun hasPlaceholder(config: Config): Boolean =
        anyString(config) { '%' in it && PLACEHOLDER.containsMatchIn(it) }

    private fun isDynamic(value: String, ownPlaceholders: Set<String>): Boolean {
        var stripped = value

        if ('%' in stripped) {
            for (name in ownPlaceholders) {
                stripped = stripped.replace("%$name%", "")
            }

            if (PLACEHOLDER.containsMatchIn(stripped)) {
                return true
            }
        }

        return "rand" in stripped
    }

    private fun anyString(value: Any?, predicate: (String) -> Boolean): Boolean = when (value) {
        is String -> predicate(value)
        is Config -> value.getKeys(false).any { anyString(value.get(it), predicate) }
        is Map<*, *> -> value.values.any { anyString(it, predicate) }
        is Iterable<*> -> value.any { anyString(it, predicate) }
        else -> false
    }
}
