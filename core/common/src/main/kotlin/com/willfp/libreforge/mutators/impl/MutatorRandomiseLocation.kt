package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorRandomiseLocation : Mutator<NoCompileData>("randomise_location") {
    override val description = "Offsets the location by a random amount along each axis."

    override val categories = setOf("location")

    override val additionalInfo = listOf(
        "Each axis is offset by a random amount between the negative and the positive of the given value."
    )

    override val arguments = arguments {
        optional(
            "x",
            description = "The maximum distance to offset the X coordinate by, in either direction.",
            type = ArgType.EXPRESSION,
            default = "0",
            example = "3"
        )
        optional(
            "y",
            description = "The maximum distance to offset the Y coordinate by, in either direction.",
            type = ArgType.EXPRESSION,
            default = "0",
            example = "1"
        )
        optional(
            "z",
            description = "The maximum distance to offset the Z coordinate by, in either direction.",
            type = ArgType.EXPRESSION,
            default = "0",
            example = "3"
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val location = data.location?.clone() ?: return data

        val x = if (config.has("x")) config.getDoubleFromExpression("x", data) else 0.0
        val y = if (config.has("y")) config.getDoubleFromExpression("y", data) else 0.0
        val z = if (config.has("z")) config.getDoubleFromExpression("z", data) else 0.0

        location.x += NumberUtils.randFloat(-x, x)
        location.y += NumberUtils.randFloat(-y, y)
        location.z += NumberUtils.randFloat(-z, z)

        return data.copy(location = location)
    }
}
