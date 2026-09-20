package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.integrations.skinsrestorer.lookupSkin
import com.willfp.libreforge.integrations.skinsrestorer.textureHash
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object MutatorSrVictimSkinHashToText : Mutator<NoCompileData>("sr_victim_skin_hash_to_text") {
    override val description = "Sets the text to the texture hash of the victim's skin."

    override val categories = setOf("player", "meta")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "No-ops if the victim isn't a player, or the victim has no skin set."
    )

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.TEXT)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val victim = data.victim as? Player ?: return data

        return data.copy(
            text = victim.lookupSkin().property?.textureHash() ?: return data
        )
    }
}
