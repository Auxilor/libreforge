package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.integrations.skinsrestorer.lookupSkin
import org.bukkit.entity.Player

object ConditionSrSkinIs : Condition<NoCompileData>("sr_skin_is") {
    override val description = "Passes when the player's skin is one of the given skins."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "Skins saved from a player are identified by that player's UUID, not their name. " +
            "URL skins are identified by their URL, and custom skins by their name."
    )

    override val arguments = arguments {
        require(
            listOf("skin", "skins"),
            "You must specify the skin(s)!",
            description = "The skin identifiers to check against.",
            type = ArgType.STRING_LIST
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val identifier = player.lookupSkin().identifier ?: return false

        return config.getStrings("skins", "skin").containsIgnoreCase(identifier.identifier)
    }
}
