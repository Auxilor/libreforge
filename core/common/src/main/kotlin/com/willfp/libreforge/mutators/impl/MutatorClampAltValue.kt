package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.injectValuesFrom
import com.willfp.libreforge.triggers.TriggerData

object MutatorClampAltValue : Mutator<NoCompileData>("clamp_alt_value") {
    override val description = "Restricts the alt value to be between a minimum and a maximum."

    override val categories = setOf("value", "meta")

    override val additionalInfo = listOf(
        "No-ops if the minimum is greater than the maximum.",
        "%value% and %altvalue% in the bounds are the live values, so they include the changes made by any mutators that ran before this one."
    )

    override val arguments = arguments {
        optional(
            "min",
            description = "The lowest that the alt value is allowed to be.",
            type = ArgType.EXPRESSION,
            default = "-Infinity",
            example = "0"
        )
        optional(
            "max",
            description = "The highest that the alt value is allowed to be.",
            type = ArgType.EXPRESSION,
            default = "Infinity",
            example = "100"
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        config.injectValuesFrom(data)

        val min = if (config.has("min")) config.getDoubleFromExpression("min", data) else Double.NEGATIVE_INFINITY
        val max = if (config.has("max")) config.getDoubleFromExpression("max", data) else Double.POSITIVE_INFINITY

        if (min > max) {
            return data
        }

        return data.copy(
            altValue = data.altValue.coerceIn(min, max)
        )
    }
}
