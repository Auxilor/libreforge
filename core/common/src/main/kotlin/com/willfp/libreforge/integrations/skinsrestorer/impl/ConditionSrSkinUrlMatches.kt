package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.skinsrestorer.lookupSkin
import com.willfp.libreforge.integrations.skinsrestorer.textureUrl
import org.bukkit.entity.Player

object ConditionSrSkinUrlMatches : Condition<NoCompileData>("sr_skin_url_matches") {
    override val description = "Passes when the texture URL of the player's skin contains the given text."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "Matches on a substring of the full texture URL, so a texture hash works here too."
    )

    override val arguments = arguments {
        require(
            "url",
            "You must specify the URL!",
            description = "The text that the skin's texture URL must contain.",
            type = ArgType.STRING
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val url = player.lookupSkin().property?.textureUrl() ?: return false

        return url.contains(config.getString("url"), ignoreCase = true)
    }
}
