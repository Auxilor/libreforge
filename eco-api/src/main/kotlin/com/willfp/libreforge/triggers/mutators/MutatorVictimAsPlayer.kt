package com.willfp.libreforge.triggers.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.triggers.DataMutator
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player

object MutatorVictimAsPlayer : DataMutator(
    "victim_as_player"
) {
    override fun mutate(data: TriggerData, config: Config): TriggerData {
        val victim = data.victim as? Player
        return data.copy(
            player = victim
        )
    }
}
