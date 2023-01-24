package com.willfp.libreforge.triggers.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.triggers.DataMutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorBlockToLocation : DataMutator(
    "block_to_location"
) {
    override fun mutate(data: TriggerData, config: Config): TriggerData {
        val location = data.location
        return data.copy(
            block = location?.block
        )
    }
}
