package com.willfp.libreforge.effects

import com.willfp.eco.core.placeholder.StaticPlaceholder

class NamedArgument constructor(
    val identifiers: Collection<String>,
    value: String
) {
    @Suppress("UNUSED")
    constructor(
        identifier: String,
        value: String
    ) : this(listOf(identifier), value)

    @Suppress("UNUSED")
    constructor(
        identifier: String,
        value: Any
    ) : this(listOf(identifier), value.toString())

    @Suppress("UNUSED")
    constructor(
        identifiers: Collection<String>,
        value: Any
    ) : this(identifiers, value.toString())

    val placeholders = identifiers.map {
        StaticPlaceholder(
            it
        ) { value }
    }

    @Deprecated(message = "Use placeholders instead, this doesn't include additional names")
    val placeholder = placeholders[0]
}
