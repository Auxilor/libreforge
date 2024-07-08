package com.willfp.libreforge.placeholders

import com.willfp.eco.core.placeholder.RegistrablePlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.util.evaluateExpressionOrNull
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.toNiceString

abstract class CustomPlaceholder(
    override val id: String
) : KRegistrable {
    abstract val placeholder: RegistrablePlaceholder

    protected fun parseValue(expression: String, ctx: PlaceholderContext): String {
        val asNumber = evaluateExpressionOrNull(
            expression,
            ctx
        )

        return asNumber?.toNiceString() ?: expression.formatEco(ctx)
    }

    override fun onRegister() {
        placeholder.register()
    }
}
