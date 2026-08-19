package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorLocationToHighestBlock : Mutator<NoCompileData>("location_to_highest_block") {
    override val description = "Moves the location up or down to the highest block at its X and Z coordinates."

    override val categories = setOf("location", "world")

    override val arguments = arguments {
        optional(
            "offset",
            description = "The amount to add to the Y coordinate of the highest block.",
            type = ArgType.EXPRESSION,
            default = "1",
            example = "1"
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val location = data.location?.clone() ?: return data
        val world = location.world ?: return data

        val offset = if (config.has("offset")) config.getDoubleFromExpression("offset", data) else 1.0

        location.y = world.getHighestBlockYAt(location).toDouble() + offset

        return data.copy(location = location)
    }
}
