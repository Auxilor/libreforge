package com.willfp.libreforge.triggers.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.triggers.DataMutator
import com.willfp.libreforge.triggers.TriggerData

class MutatorPlayerAsVictim : DataMutator("player_as_victim") {
    override fun mutate(data: TriggerData, config: Config): TriggerData {
        val player = data.player ?: return data
        return data.copy(
            victim = player
        )
    }
}
