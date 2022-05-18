package com.willfp.libreforge.effects

import com.willfp.eco.core.placeholder.StaticPlaceholder

class NamedArgument internal constructor(
    identifier: String,
    value: String
) {
    val placeholder = StaticPlaceholder(
        identifier
    ) { value }
}
