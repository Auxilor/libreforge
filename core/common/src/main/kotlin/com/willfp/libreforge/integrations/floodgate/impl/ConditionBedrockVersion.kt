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
import org.bukkit.entity.Player

object ConditionBedrockVersion : Condition<NoCompileData>("bedrock_version") {
    override val description = "Passes when the player is on Bedrock edition running one of the given client versions."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Never passes for Java players, as there is no Bedrock client version for them.",
        "Matching is by prefix, so 1.21 matches 1.21.0 and 1.21.40 alike.",
        "This is a match against a list, not a minimum version check; list every version you want to accept."
    )

    override val arguments = arguments {
        require(
            listOf("versions", "version"),
            "You must specify the version(s)!",
            description = "The Bedrock client versions to match, as a prefix of the full version string.",
            type = ArgType.STRING_LIST,
            example = listOf("1.21")
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val version = bedrockPlayerOf(player)?.version ?: return false

        return config.getStrings("versions", "version").any { version.startsWith(it, ignoreCase = true) }
    }
}
