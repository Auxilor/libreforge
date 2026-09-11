package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.integrations.skinsrestorer.lookupSkin
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorSrSkinNameToText : Mutator<NoCompileData>("sr_skin_name_to_text") {
    override val description = "Sets the text to the identifier of the player's skin."

    override val categories = setOf("player", "meta")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "Skins saved from a player are identified by that player's UUID, not their name. " +
            "URL skins are identified by their URL, and custom skins by their name.",
        "No-ops if there is no player, or the player has no skin set."
    )

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.TEXT)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val player = data.player ?: return data

        return data.copy(
            text = player.lookupSkin().identifier?.identifier ?: return data
        )
    }
}
