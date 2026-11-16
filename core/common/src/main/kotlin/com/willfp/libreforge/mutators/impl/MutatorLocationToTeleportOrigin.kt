package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.player.PlayerTeleportEvent

object MutatorLocationToTeleportOrigin : Mutator<NoCompileData>("location_to_teleport_origin") {
    override val description = "Sets the location to where the player was before the teleport."

    override val categories = setOf("location", "movement")

    override val additionalInfo = listOf("Only applies to the teleport trigger. Other triggers keep their location.")

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.EVENT becomes TriggerParameter.LOCATION
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val event = data.event as? PlayerTeleportEvent ?: return data

        return data.copy(
            location = event.from.clone()
        )
    }
}
