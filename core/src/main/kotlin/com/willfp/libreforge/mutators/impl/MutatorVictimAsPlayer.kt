package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object MutatorVictimAsPlayer : Mutator<NoCompileData>("victim_as_player") {
    override val parameterTransformers = parameterTransformers {
        TriggerParameter.VICTIM becomes TriggerParameter.PLAYER
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            player = data.victim as? Player
        )
    }
}
