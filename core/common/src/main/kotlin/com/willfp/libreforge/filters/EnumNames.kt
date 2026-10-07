package com.willfp.libreforge.filters

import java.util.Locale

/**
 * Config strings matched against enum constant names, ignoring case, by set lookup. Immutable.
 */
internal class EnumNames(
    private val names: List<String>
) : List<String> by names {
    private val uppercase: Set<String> = names.mapTo(HashSet()) { it.uppercase(Locale.ROOT) }

    /**
     * If [name], an enum constant name, is in the list ignoring case.
     */
    fun hasName(name: String): Boolean =
        name in uppercase
}

/**
 * If [name], an enum constant name, is in this list ignoring case.
 */
internal fun Collection<String>.hasEnumName(name: String): Boolean =
    (this as? EnumNames)?.hasName(name) ?: this.any { it.equals(name, ignoreCase = true) }
