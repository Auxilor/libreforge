package com.willfp.libreforge.effects.arguments.custom

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.triggers.DispatchedTrigger

class ArgumentCustom(private val customID: String) : EffectArgument<CustomEffectArgument?>("custom_$customID") {
    private fun DispatchedTrigger.addCustomPlaceholders(element: ConfigurableElement) {
        val argSection = element.config.getSubsection(id)
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
        return CustomEffectArguments[customID]
    }
}
