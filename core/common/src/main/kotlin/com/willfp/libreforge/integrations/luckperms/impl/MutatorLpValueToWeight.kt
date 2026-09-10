package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.integrations.luckperms.lpMetaData
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorLpValueToWeight : Mutator<NoCompileData>("lp_value_to_weight") {
    override val description = "Sets the value to the LuckPerms weight of the player."

    override val categories = setOf("value", "permission")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "No-ops if the LuckPerms data of the player is not loaded."
    )

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.VALUE)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val weight = data.player?.lpMetaData?.weight ?: return data

        return data.copy(
            value = weight.toDouble()
        )
    }
}
