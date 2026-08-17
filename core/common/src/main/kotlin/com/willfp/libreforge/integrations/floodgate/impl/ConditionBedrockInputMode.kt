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
import org.geysermc.floodgate.util.InputMode

object ConditionBedrockInputMode : Condition<NoCompileData>("bedrock_input_mode") {
    override val description = "Passes when the player is on Bedrock edition using one of the given input methods."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Never passes for Java players, as there is no input information for them.",
        "Useful for making abilities fairer on touch, which cannot aim as precisely as a mouse."
    )

    override val arguments = arguments {
        require(
            listOf("modes", "mode"),
            "You must specify the input mode(s)!",
            description = "The Bedrock input modes to match.",
            type = ArgType.STRING_LIST,
            choices = InputMode.values().map { it.name },
            example = listOf("TOUCH", "CONTROLLER")
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

        return config.getStrings("modes", "mode").namesEnum(bedrock.inputMode)
    }
}
