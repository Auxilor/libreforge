package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.get
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.block.Block

object MutatorDispatcherAsBlock : Mutator<NoCompileData>("dispatcher_as_block") {
    override val description = "Sets the block to the current dispatcher."

    override val categories = setOf("block", "meta")

    override val additionalInfo = listOf("No-ops if the dispatcher is not a block.")

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.BLOCK)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            block = data.dispatcher.get<Block>() ?: return data
        )
    }
}
