package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.get
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorDispatcherAsPlayer : Mutator<NoCompileData>("dispatcher_as_player") {
    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.PLAYER)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            player = data.dispatcher.get()
        )
    }
}
