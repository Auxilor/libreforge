package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData

object MutatorBlockAsDispatcher : Mutator<NoCompileData>("block_as_dispatcher") {
    override val description = "Sets the dispatcher to the current block."

    override val categories = setOf("block", "meta")

    override val additionalInfo = listOf("No-ops if the trigger data has no block.")

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            dispatcher = data.block?.toDispatcher() ?: return data
        )
    }
}
