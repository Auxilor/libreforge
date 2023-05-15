package com.willfp.libreforge.placeholders

import com.willfp.eco.core.placeholder.RegistrablePlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.util.evaluateExpression
import com.willfp.eco.util.formatEco

abstract class CustomPlaceholder(
    override val id: String
) : KRegistrable {
    abstract val placeholder: RegistrablePlaceholder

    protected fun getValue(expression: String, ctx: PlaceholderContext): String {
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

    override fun onRegister() {
        placeholder.register()
    }
}
