package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.integrations.luckperms.lpMetaData
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorLpValueToMeta : Mutator<NoCompileData>("lp_value_to_meta") {
    override val description = "Sets the value to a numeric LuckPerms meta value of the player."

    override val categories = setOf("value", "permission")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "No-ops if the meta key is not set or is not a number."
    )

    override val arguments = arguments {
        require(
            "key",
            "You must specify the meta key!",
            description = "The LuckPerms meta key to read.",
            type = ArgType.STRING,
            example = "mine-tier"
        )
    }

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.VALUE)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val meta = data.player?.lpMetaData?.getMetaValue(config.getString("key"))?.toDoubleOrNull() ?: return data

        return data.copy(
            value = meta
        )
    }
}
