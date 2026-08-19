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

object MutatorVelocityTowardsLocation : Mutator<NoCompileData>("velocity_towards_location") {
    override val description = "Sets the velocity to point from an entity towards the location."

    override val categories = setOf("movement", "location")

    override val additionalInfo = listOf(
        "No-ops if the entity is missing, in a different world, or standing exactly at the location.",
        "Use a negative strength to point away from the location instead."
    )

    override val arguments = arguments {
        require("from", "You must specify what to move! (player or victim)", Config::getString) {
            it in listOf("player", "victim")
        }
        describe(
            "from",
            description = "The entity that the velocity points away from.",
            type = ArgType.STRING,
            choices = listOf("player", "victim")
        )
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

        val from = if (config.getString("from").equals("player", true)) {
            data.player
        } else {
            data.victim
        }?.location ?: return data

        if (from.world == null || from.world != location.world) {
            return data
        }

        val direction = location.toVector().subtract(from.toVector())

        if (direction.lengthSquared() == 0.0) {
            return data
        }

        return data.copy(
            velocity = direction.normalize()
                .multiply(config.getDoubleFromExpression("strength", data))
        )
    }
}
