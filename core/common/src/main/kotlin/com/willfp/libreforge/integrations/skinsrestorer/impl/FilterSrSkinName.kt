package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.skinsrestorer.lookupSkin
import com.willfp.libreforge.triggers.TriggerData

object FilterSrSkinName : Filter<NoCompileData, Collection<String>>("sr_skin_name") {
    override val description = "Matches when the player's skin is one of the given skins."

    override val categories = setOf("player")

    override val valueType = ArgType.STRING_LIST

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "Skins saved from a player are identified by that player's UUID, not their name. " +
            "URL skins are identified by their URL, and custom skins by their name.",
        "Passes automatically when there is no player, or the player has no skin set."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val player = data.player ?: return true

        val identifier = player.lookupSkin().identifier ?: return true

        return value.containsIgnoreCase(identifier.identifier)
    }
}
