package com.willfp.libreforge

import com.willfp.eco.core.placeholder.StaticPlaceholder

class GroupedStaticPlaceholder constructor(
    identifiers: Collection<String>,
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

    val placeholders = identifiers.map {
        StaticPlaceholder(
            it
        ) { value }
    }
}
