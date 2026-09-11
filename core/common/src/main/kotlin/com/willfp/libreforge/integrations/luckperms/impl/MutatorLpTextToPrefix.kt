package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.integrations.luckperms.lpMetaData
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorLpTextToPrefix : Mutator<NoCompileData>("lp_text_to_prefix") {
    override val description = "Sets the text to the active LuckPerms prefix of the player."

    override val categories = setOf("chat", "permission")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "No-ops if the player has no prefix."
    )

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.TEXT)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val prefix = data.player?.lpMetaData?.prefix ?: return data

        return data.copy(
            text = prefix
        )
    }
}
