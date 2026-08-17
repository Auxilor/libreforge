package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.integrations.floodgate.bedrockPlayerOf
import com.willfp.libreforge.integrations.floodgate.namesEnum
import org.bukkit.entity.Player
import org.geysermc.floodgate.util.UiProfile

object ConditionBedrockUiProfile : Condition<NoCompileData>("bedrock_ui_profile") {
    override val description = "Passes when the player is on Bedrock edition using one of the given UI profiles."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Never passes for Java players, as there is no UI information for them.",
        "POCKET is the compact touch interface; CLASSIC is the full inventory interface.",
        "Pocket UI shows far fewer inventory slots, so it is worth checking before opening a large menu."
    )

    override val arguments = arguments {
        require(
            listOf("profiles", "profile"),
            "You must specify the UI profile(s)!",
            description = "The Bedrock UI profiles to match.",
            type = ArgType.STRING_LIST,
            choices = UiProfile.values().map { it.name },
            example = listOf("POCKET")
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val bedrock = bedrockPlayerOf(player) ?: return false

        return config.getStrings("profiles", "profile").namesEnum(bedrock.uiProfile)
    }
}
