package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorLocationToGround : Mutator<NoCompileData>("location_to_ground") {
    override val description = "Moves the location down to rest on top of the first solid block beneath it."

    override val categories = setOf("location", "world")

    override val additionalInfo = listOf("No-ops if there is no solid block beneath the location.")

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val location = data.location?.clone() ?: return data
        val world = location.world ?: return data

        for (y in location.blockY downTo world.minHeight) {
            if (world.getBlockAt(location.blockX, y, location.blockZ).type.isSolid) {
                location.y = (y + 1).toDouble()
                return data.copy(location = location)
            }
        }

        return data
    }
}
