package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorMultiplyVelocity : Mutator<NoCompileData>("multiply_velocity") {
    override val description = "Scales the velocity by a multiplier, keeping its direction."

    override val categories = setOf("movement", "meta")

    override val additionalInfo = listOf("A negative multiplier reverses the direction of the velocity.")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The amount to multiply the velocity by.",
            type = ArgType.EXPRESSION,
            example = "1.5"
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val velocity = data.velocity?.clone() ?: return data

        return data.copy(
            velocity = velocity.multiply(config.getDoubleFromExpression("multiplier", data))
        )
    }
}
