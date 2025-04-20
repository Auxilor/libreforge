package com.willfp.libreforge.integrations.paper.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.kyori.adventure.text.minimessage.MiniMessage

object EffectSendMinimessage : Effect<NoCompileData>("send_minimessage") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(listOf("message", "messages"), "You must specify the message(s) to send!")
    }

    private val miniMessage = MiniMessage.miniMessage()

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val messages = config.getStrings("messages", "message")
            .map { it.replace("%player%", player.name) }
            .map { PlaceholderManager.translatePlaceholders(it, config.toPlaceholderContext(data)) }
            .dropLastWhile { it.isEmpty() }
            .map { miniMessage.deserialize(it) }

        val actionBar = config.getBool("action_bar")

        if (actionBar) {
            player.sendActionBar(messages.first())
        } else {
            for (s in messages) {
                player.sendMessage(s)
            }
        }

        return true
    }
}
