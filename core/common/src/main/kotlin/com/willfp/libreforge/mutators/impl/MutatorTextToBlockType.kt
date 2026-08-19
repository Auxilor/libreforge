package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorTextToBlockType : Mutator<NoCompileData>("text_to_block_type") {
    override val description = "Sets the text to the material name of the block, e.g. diamond_ore."

    override val categories = setOf("chat", "block")

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.BLOCK becomes TriggerParameter.TEXT
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            text = data.block?.type?.name?.lowercase()
        )
    }
}
