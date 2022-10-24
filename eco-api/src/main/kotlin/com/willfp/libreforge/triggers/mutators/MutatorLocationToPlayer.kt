package com.willfp.libreforge.triggers.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.triggers.DataMutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorLocationToPlayer : DataMutator(
    "location_to_player"
) {
    override fun mutate(data: TriggerData, config: Config): TriggerData {
        val player = data.player
        return data.copy(
            location = player?.location
        )
    }
}
