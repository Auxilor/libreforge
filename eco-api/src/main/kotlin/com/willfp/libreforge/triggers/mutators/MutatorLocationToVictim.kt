package com.willfp.libreforge.triggers.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.triggers.DataMutator
import com.willfp.libreforge.triggers.MutationOrder
import com.willfp.libreforge.triggers.TriggerData

class MutatorLocationToVictim : DataMutator(
    "location_to_victim",
    order = MutationOrder.EARLY
) {
    override fun mutate(data: TriggerData, config: Config): TriggerData {
        val victim = data.victim ?: return data
        return data.copy(
            location = victim.location
        )
    }
}
