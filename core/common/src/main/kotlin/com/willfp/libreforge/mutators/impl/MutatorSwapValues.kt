package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorSwapValues : Mutator<NoCompileData>("swap_values") {
    override val description = "Swaps the value and the alt value around."

    override val categories = setOf("value", "meta")

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.VALUE becomes TriggerParameter.ALT_VALUE
        TriggerParameter.ALT_VALUE becomes TriggerParameter.VALUE
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            value = data.altValue,
            altValue = data.value
        )
    }
}
