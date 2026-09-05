package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.integrations.floodgate.floodgate
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorBedrockPlayerCountAsValue : Mutator<NoCompileData>("bedrock_player_count_as_value") {
    override val description = "Sets the value to the number of Bedrock players currently online."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Counts every online Bedrock player on this server, not only those near the trigger."
    )

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.VALUE)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(value = floodgate.playerCount.toDouble())
    }
}
