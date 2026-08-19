package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorVelocityToDirection : Mutator<NoCompileData>("velocity_to_direction") {
    override val description = "Sets the velocity to point in the direction that the location is facing."

    override val categories = setOf("movement", "location")

    override val arguments = arguments {
        require(
            "strength",
            "You must specify the strength!",
            description = "The length of the resulting velocity vector.",
            type = ArgType.EXPRESSION,
            example = "1.5"
        )
    }

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.LOCATION becomes TriggerParameter.VELOCITY
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val location = data.location ?: return data

        return data.copy(
            velocity = location.direction.normalize()
                .multiply(config.getDoubleFromExpression("strength", data))
        )
    }
}
