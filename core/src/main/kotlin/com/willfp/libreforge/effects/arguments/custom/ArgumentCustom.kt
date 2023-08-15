package com.willfp.libreforge.effects.arguments.custom

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.effects.arguments.custom.CustomEffectArgument
import com.willfp.libreforge.effects.arguments.custom.CustomEffectArguments
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.checkerframework.checker.units.qual.C

class ArgumentCustom(id: String) : EffectArgument<CustomEffectArgument?>("custom_$id") {
    private fun DispatchedTrigger.addCustomPlaceholders(element: ConfigurableElement) {
        val argSection = element.config.getSubsection("custom_$id")
        for (key in argSection.getKeys(true)) {
            this.addPlaceholder(NamedValue(key, argSection.getString(key)))
        }
    }

    override fun isMet(
        element: ConfigurableElement,
        trigger: DispatchedTrigger,
        compileData: CustomEffectArgument?
    ): Boolean {
        trigger.addCustomPlaceholders(element)

        return compileData?.isMet?.areMetAndTrigger(trigger) ?: false
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
        return CustomEffectArguments[id]
    }
}
