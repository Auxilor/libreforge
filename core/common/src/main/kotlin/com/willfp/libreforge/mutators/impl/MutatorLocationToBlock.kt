package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorLocationToBlock : Mutator<NoCompileData>("location_to_block") {
    override val description = "Re-maps the block parameter to use the block's location."

    override val categories = setOf("location", "block")

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.BLOCK becomes TriggerParameter.LOCATION
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            location = data.block?.location
        )
    }
}
