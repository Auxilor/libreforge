package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.integrations.floodgate.bedrockPlayerOf
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorLinkedJavaNameAsText : Mutator<NoCompileData>("linked_java_name_as_text") {
    override val description = "Sets the text to the name of the Java account a Bedrock player has linked."

    override val categories = setOf("player", "text")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Leaves the trigger data untouched for Java players, and for Bedrock players with no linked account."
    )

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.TEXT)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val linked = bedrockPlayerOf(data.player)?.linkedPlayer ?: return data

        return data.copy(text = linked.javaUsername)
    }
}
