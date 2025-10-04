package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData

object MutatorVictimAsDispatcher : Mutator<NoCompileData>("victim_as_dispatcher") {
    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            dispatcher = data.victim?.toDispatcher() ?: return data
        )
    }
}
