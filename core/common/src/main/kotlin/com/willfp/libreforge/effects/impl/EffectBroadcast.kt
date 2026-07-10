package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit

object EffectBroadcast : Effect<NoCompileData>("broadcast") {
    override val description = "Broadcasts one or more messages to all online players."
    override val categories = setOf("chat")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            listOf("message", "messages"),
            "You must specify the message(s) to send!",
            description = "The message(s) to broadcast to all players.",
            type = ArgType.STRING_LIST,
            example = listOf("&aWelcome to the server, %player%!")
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val messages = config.getStrings("messages", "message")
            .map { it.replace("%player%", data.player?.name ?: "%player%") }
            .formatEco(config.toPlaceholderContext(data))
            .dropLastWhile { it.isEmpty() }

        for (message in messages) {
            @Suppress("DEPRECATION")
            Bukkit.getServer().broadcastMessage(message)
        }

        return true
    }
}
