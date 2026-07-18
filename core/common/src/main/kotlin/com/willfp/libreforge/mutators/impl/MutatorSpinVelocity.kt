package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorSpinVelocity : Mutator<NoCompileData>("spin_velocity") {
    override val description = "Rotates the velocity vector around the Y axis by the given angle."

    override val categories = setOf("meta")

    override val arguments = arguments {
        require(
            "angle",
            "You must specify the angle to rotate by!",
            description = "The angle in degrees to rotate the velocity around the Y axis.",
            type = ArgType.EXPRESSION,
            example = "%level% * 5"
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val velocity = data.velocity?.clone() ?: return data

        val newVelocity = velocity
            .rotateAroundY(Math.toRadians(config.getDoubleFromExpression("angle", data)))

        return data.copy(
            velocity = newVelocity
        )
    }
}