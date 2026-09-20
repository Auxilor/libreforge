package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.skinsrestorer.lookupSkin
import com.willfp.libreforge.integrations.skinsrestorer.textureHash
import com.willfp.libreforge.triggers.TriggerData

object FilterSrSkinTextureHash : Filter<NoCompileData, Collection<String>>("sr_skin_texture_hash") {
    override val description = "Matches when the texture hash of the player's skin is one of the given hashes."

    override val categories = setOf("player")

    override val valueType = ArgType.STRING_LIST

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "Passes automatically when there is no player, or the player has no skin set."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val player = data.player ?: return true

        val hash = player.lookupSkin().property?.textureHash() ?: return true

        return value.containsIgnoreCase(hash)
    }
}
