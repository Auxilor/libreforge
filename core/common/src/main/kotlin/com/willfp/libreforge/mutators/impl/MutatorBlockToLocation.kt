package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorBlockToLocation : Mutator<NoCompileData>("block_to_location") {
    override val description = "Sets the location parameter to derive the block at that position."

    override val categories = setOf("location", "block")

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.LOCATION becomes TriggerParameter.BLOCK
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            block = data.location?.block
        )
    }
}
