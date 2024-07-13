package com.willfp.libreforge

import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.templates.SimpleInjectablePlaceholder

open class NamedValue constructor(
    val identifiers: Collection<String>,
    value: String
) {
    constructor(
        identifier: String,
        value: Any
    ) : this(listOf(identifier), value.toString())

    constructor(
        identifiers: Collection<String>,
        value: Any
    ) : this(identifiers, value.toString())

    open val placeholders: List<InjectablePlaceholder> = identifiers.map {
        StaticPlaceholder(
            it
        ) { value }
    }
}

/*

Ideally, this would be merged into NamedValue, but that would break backwards compatibility,
because the () -> Any constructor would be ambiguous with the Any constructor, and casting
doesn't seem to fix the problem either.

This is internal because one: it's a hack, and two: there isn't really a case where this
would be needed outside of repeats, which is a purely internal feature anyway.

Because of how expression caching works, DynamicNamedValue also needs to provide placeholders
that give different hashes each time, to force a re-calculation of the expression.

 */

internal class DynamicNumericValue(
    identifiers: Collection<String>,
    value: Number
) : NamedValue(identifiers, value) {
    constructor(
        identifier: String,
        value: Number
    ) : this(listOf(identifier), value)

    override val placeholders: List<InjectablePlaceholder> = identifiers.map {
        ValueHashPlaceholder(
            it,
            value
        )
    }

    private class ValueHashPlaceholder(
        private val identifier: String,
        private val value: Number
    ) : SimpleInjectablePlaceholder(identifier) {
        override fun getValue(p0: String, p1: PlaceholderContext) = value.toString()

        override fun hashCode(): Int {
            // Use the value of the function to force a re-calculation of the expression
            return value.toInt() * 31 + identifier.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }

            if (other !is ValueHashPlaceholder) {
                return false
            }

            return other.identifier == this.identifier
                    && other.value == this.value
        }
    }
}


fun Collection<NamedValue>.mapToPlaceholders(): Array<out InjectablePlaceholder> {
    return this.flatMap { it.placeholders }.toTypedArray()
}
