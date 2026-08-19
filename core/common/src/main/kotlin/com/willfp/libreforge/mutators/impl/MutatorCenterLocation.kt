package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Location

object MutatorCenterLocation : Mutator<NoCompileData>("center_location") {
    override val description = "Moves the location to the center of the block that it is inside."

    override val categories = setOf("location", "block")

    override val arguments = arguments {
        optional(
            "center_y",
            description = "If the Y coordinate should be centered too, rather than kept as-is.",
            type = ArgType.BOOLEAN,
            default = "false",
            example = "true"
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val location = data.location ?: return data
        val centerY = config.has("center_y") && config.getBool("center_y")

        return data.copy(
            location = Location(
                location.world,
                location.blockX + 0.5,
                if (centerY) location.blockY + 0.5 else location.y,
                location.blockZ + 0.5,
                location.yaw,
                location.pitch
            )
        )
    }
}
