package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.skinsrestorer.lookupSkin
import com.willfp.libreforge.integrations.skinsrestorer.skinTypeChoices
import net.skinsrestorer.api.property.SkinType
import org.bukkit.entity.Player

object ConditionSrSkinTypeIs : Condition<NoCompileData>("sr_skin_type_is") {
    override val description = "Passes when the player's skin came from the given source."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires SkinsRestorer to be installed.")

    override val arguments = arguments {
        require(
            "type",
            "You must specify the skin type!",
            description = "The source of the skin: PLAYER for a skin taken from another player, " +
                "URL for a generated skin, CUSTOM for a skin saved by an admin, or LEGACY.",
            type = ArgType.STRING,
            choices = skinTypeChoices
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

        val type = enumValueOfOrNull<SkinType>(config.getString("type").uppercase()) ?: return false

        return identifier.skinType == type
    }
}
