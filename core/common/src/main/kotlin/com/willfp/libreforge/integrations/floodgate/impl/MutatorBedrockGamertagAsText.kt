package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.integrations.floodgate.bedrockPlayerOf
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorBedrockGamertagAsText : Mutator<NoCompileData>("bedrock_gamertag_as_text") {
    override val description = "Sets the text to the player's raw Bedrock gamertag, without the Floodgate prefix."

    override val categories = setOf("player", "text")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Leaves the trigger data untouched for Java players, who have no gamertag.",
        "This is the real Xbox gamertag, so it keeps spaces and its original casing, unlike the name the server uses."
    )

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.TEXT)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val gamertag = bedrockPlayerOf(data.player)?.username ?: return data

        return data.copy(text = gamertag)
    }
}
