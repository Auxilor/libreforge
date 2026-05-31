package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectKick : Effect<NoCompileData>("kick") {
    override val description = "Kicks the player from the server with a specified message."
    override val categories = setOf("player")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "message",
            "You must specify the message to kick with!",
            description = "The kick screen message shown to the player.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        @Suppress("DEPRECATION")
        player.kickPlayer(
            config.getFormattedString("message", config.toPlaceholderContext(data))
        )

        return true
    }
}
