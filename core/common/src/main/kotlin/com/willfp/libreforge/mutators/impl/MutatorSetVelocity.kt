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
import org.bukkit.util.Vector

object MutatorSetVelocity : Mutator<NoCompileData>("set_velocity") {
    override val description = "Sets the velocity to a fixed vector."

    override val categories = setOf("movement", "meta")

    override val arguments = arguments {
        optional(
            "x",
            description = "The X component of the velocity.",
            type = ArgType.EXPRESSION,
            default = "0",
            example = "0"
        )
        optional(
            "y",
            description = "The Y component of the velocity.",
            type = ArgType.EXPRESSION,
            default = "0",
            example = "1.2"
        )
        optional(
            "z",
            description = "The Z component of the velocity.",
            type = ArgType.EXPRESSION,
            default = "0",
            example = "0"
        )
    }

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.VELOCITY)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val x = if (config.has("x")) config.getDoubleFromExpression("x", data) else 0.0
        val y = if (config.has("y")) config.getDoubleFromExpression("y", data) else 0.0
        val z = if (config.has("z")) config.getDoubleFromExpression("z", data) else 0.0

        return data.copy(
            velocity = Vector(x, y, z)
        )
    }
}
