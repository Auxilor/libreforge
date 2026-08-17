package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.integrations.floodgate.bedrockPlayerOf
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorBedrockXuidAsText : Mutator<NoCompileData>("bedrock_xuid_as_text") {
    override val description = "Sets the text to the player's Xbox Live ID."

    override val categories = setOf("player", "text")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Leaves the trigger data untouched for Java players, who have no Xbox Live ID.",
        "The XUID never changes for an account, unlike the gamertag, so it is the stable way to identify a Bedrock player externally."
    )

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.TEXT)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val xuid = bedrockPlayerOf(data.player)?.xuid ?: return data

        return data.copy(text = xuid)
    }
}
