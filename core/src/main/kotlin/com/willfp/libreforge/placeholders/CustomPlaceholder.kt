package com.willfp.libreforge.placeholders

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.templates.SimplePlaceholder
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.util.evaluateExpression
import com.willfp.libreforge.plugin

class CustomPlaceholder(
    override val id: String,
    val expression: String
) : KRegistrable {
    val placeholder = object : SimplePlaceholder(plugin, id) {
        override fun getValue(args: String, ctx: PlaceholderContext): String {
            val asNumber = evaluateExpression(
                expression,
                ctx
            )

            return if (asNumber != 0.0) {
                asNumber.toString()
            } else {
                expression
            }
        }
    }

    override fun onRegister() {
        placeholder.register()
    }
}
