package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Location

object MutatorRoundLocation : Mutator<NoCompileData>("round_location") {
    override val description = "Snaps the location to the corner of the block that it is inside."

    override val categories = setOf("location", "block")

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val location = data.location ?: return data

        return data.copy(
            location = Location(
                location.world,
                location.blockX.toDouble(),
                location.blockY.toDouble(),
                location.blockZ.toDouble(),
                location.yaw,
                location.pitch
            )
        )
    }
}
