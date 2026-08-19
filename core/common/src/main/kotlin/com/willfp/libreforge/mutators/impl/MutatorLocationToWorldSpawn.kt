package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorLocationToWorldSpawn : Mutator<NoCompileData>("location_to_world_spawn") {
    override val description = "Sets the location to the spawn point of the location's world."

    override val categories = setOf("location", "world")

    override val additionalInfo = listOf("No-ops if there is no location to read the world from.")

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val world = data.location?.world ?: return data

        return data.copy(
            location = world.spawnLocation
        )
    }
}
