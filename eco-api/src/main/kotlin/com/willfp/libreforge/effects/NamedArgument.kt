package com.willfp.libreforge.effects

import com.willfp.eco.core.placeholder.StaticPlaceholder

class NamedArgument internal constructor(
    identifiers: Collection<String>,
    value: String
) {
    @Suppress("UNUSED")
    internal constructor(
        identifier: String,
        value: String
    ) : this(listOf(identifier), value)

    @Suppress("UNUSED")
    internal constructor(
        identifier: String,
        value: Any
    ) : this(listOf(identifier), value.toString())

    val placeholders = identifiers.map {
        StaticPlaceholder(
            it
        ) { value }
    }

    @Deprecated(message = "Use placeholders instead, this doesn't include additional names")
    val placeholder = placeholders[0]
}
