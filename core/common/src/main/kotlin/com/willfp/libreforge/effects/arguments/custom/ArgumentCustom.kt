package com.willfp.libreforge.effects.arguments.custom

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.evaluateExpressionOrNull
import com.willfp.eco.util.toNiceString
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.DispatchedTrigger

class ArgumentCustom(private val customID: String) : EffectArgument<CustomEffectArgument?>("custom_$customID") {
    private fun DispatchedTrigger.addCustomPlaceholders(element: ConfigurableElement) {
        val argSection = element.config.getSubsection(id)
        val context = element.config.toPlaceholderContext(data)
        for (key in argSection.getKeys(true)) {
            this.addPlaceholder(NamedValue(key, argSection.getString(key).evaluateIfExpression(context)))
        }
    }

    private fun String.evaluateIfExpression(context: PlaceholderContext): String {
        if (this.none { it in EXPRESSION_OPERATORS }) {
            return this
        }

        return evaluateExpressionOrNull(this, context)?.toNiceString() ?: this
    }

    private companion object {
        private const val EXPRESSION_OPERATORS = "+-*/^()"
    }

    override fun isMet(
        element: ConfigurableElement,
        trigger: DispatchedTrigger,
        compileData: CustomEffectArgument?
    ): Boolean {
        compileData ?: return false

        trigger.addCustomPlaceholders(element)

        for (config in compileData.conditionConfigsToInject) {
            config.addInjectablePlaceholder(trigger.placeholders)
        }

        return compileData.isMet.areMetAndTrigger(trigger)
    }


    override fun ifMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: CustomEffectArgument?) {
        trigger.addCustomPlaceholders(element)

        compileData?.ifMet?.trigger(trigger)
    }

    override fun ifNotMet(
        element: ConfigurableElement,
        trigger: DispatchedTrigger,
        compileData: CustomEffectArgument?
    ) {
        trigger.addCustomPlaceholders(element)

        compileData?.ifNotMet?.trigger(trigger)
    }

    override fun makeCompileData(config: Config, context: ViolationContext): CustomEffectArgument? {
        return CustomEffectArguments[customID]
    }
}
