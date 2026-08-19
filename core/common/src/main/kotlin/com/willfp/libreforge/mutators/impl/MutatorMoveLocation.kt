package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData
import kotlin.math.cos
import kotlin.math.sin

object MutatorMoveLocation : Mutator<NoCompileData>("move_location") {
    override val description = "Offsets the location relative to the direction that it is facing."

    override val categories = setOf("location", "movement")

    override val additionalInfo = listOf(
        "Forwards and sideways offsets ignore the pitch, so they never move the location vertically."
    )

    override val arguments = arguments {
        optional(
            "forwards",
            description = "The distance to move in the direction the location is facing. Negative values move backwards.",
            type = ArgType.EXPRESSION,
            default = "0",
            example = "2"
        )
        optional(
            "sideways",
            description = "The distance to move to the right of the direction the location is facing. Negative values move left.",
            type = ArgType.EXPRESSION,
            default = "0",
            example = "-1.5"
        )
        optional(
            "upwards",
            description = "The distance to move upwards. Negative values move downwards.",
            type = ArgType.EXPRESSION,
            default = "0",
            example = "1"
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val location = data.location?.clone() ?: return data

        val forwards = if (config.has("forwards")) config.getDoubleFromExpression("forwards", data) else 0.0
        val sideways = if (config.has("sideways")) config.getDoubleFromExpression("sideways", data) else 0.0
        val upwards = if (config.has("upwards")) config.getDoubleFromExpression("upwards", data) else 0.0

        val yaw = Math.toRadians(location.yaw.toDouble())

        // The horizontal facing direction; rotating it 90 degrees clockwise gives the right-hand direction.
        val forwardX = -sin(yaw)
        val forwardZ = cos(yaw)

        location.x += forwardX * forwards - forwardZ * sideways
        location.z += forwardZ * forwards + forwardX * sideways
        location.y += upwards

        return data.copy(location = location)
    }
}
