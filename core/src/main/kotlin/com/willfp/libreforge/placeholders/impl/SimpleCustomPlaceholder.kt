package com.willfp.libreforge.placeholders.impl

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.placeholder.RegistrablePlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.templates.SimplePlaceholder
import com.willfp.eco.util.evaluateExpression
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.placeholders.CustomPlaceholder

class SimpleCustomPlaceholder(
    id: String,
    val expression: String,
    plugin: EcoPlugin
) : CustomPlaceholder(id) {
    override val placeholder: RegistrablePlaceholder = object : SimplePlaceholder(plugin, id) {
        override fun getValue(args: String, ctx: PlaceholderContext): String {
            val asNumber = evaluateExpression(
                expression,
                ctx
            )

            return if (asNumber != 0.0) {
                asNumber.toString()
            } else {
                expression.formatEco(ctx)
            }
        }
    }
}
