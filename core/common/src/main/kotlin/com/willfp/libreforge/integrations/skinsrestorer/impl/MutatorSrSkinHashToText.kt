package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.integrations.skinsrestorer.lookupSkin
import com.willfp.libreforge.integrations.skinsrestorer.textureHash
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorSrSkinHashToText : Mutator<NoCompileData>("sr_skin_hash_to_text") {
    override val description = "Sets the text to the texture hash of the player's skin."

    override val categories = setOf("player", "meta")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "No-ops if there is no player, or the player has no skin set."
    )

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.TEXT)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val player = data.player ?: return data

        return data.copy(
            text = player.lookupSkin().property?.textureHash() ?: return data
        )
    }
}
