package com.willfp.libreforge.triggers.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.triggers.DataMutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorLocationToVictim : DataMutator(
    "location_to_victim"
) {
    override fun mutate(data: TriggerData, config: Config): TriggerData {
        val victim = data.victim
        return data.copy(
            location = victim?.location
        )
    }
}
