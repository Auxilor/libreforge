package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.injectValuesFrom
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorSetValue : Mutator<NoCompileData>("set_value") {
    override val description = "Sets the value to the result of an expression."

    override val categories = setOf("value", "meta")

    override val additionalInfo = listOf(
        "%value% and %altvalue% in the expression are the live values, so they include the changes made by any mutators that ran before this one."
    )

    override val arguments = arguments {
        require(
            "value",
            "You must specify the value!",
            description = "The expression to set the value to.",
            type = ArgType.EXPRESSION,
            example = "%value% * 2"
        )
    }

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.VALUE)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        config.injectValuesFrom(data)

        return data.copy(
            value = config.getDoubleFromExpression("value", data)
        )
    }
}
