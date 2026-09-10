package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.integrations.luckperms.lpUser
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorLpTextToPrimaryGroup : Mutator<NoCompileData>("lp_text_to_primary_group") {
    override val description = "Sets the text to the primary LuckPerms group of the player."

    override val categories = setOf("chat", "permission")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "No-ops if the LuckPerms data of the player is not loaded."
    )

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.TEXT)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val user = data.player?.lpUser ?: return data

        return data.copy(
            text = user.primaryGroup
        )
    }
}
