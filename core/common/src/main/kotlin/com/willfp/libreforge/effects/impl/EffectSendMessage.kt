package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.asAudience
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.toComponent
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSendMessage : Effect<NoCompileData>("send_message") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(listOf("message", "messages"), "You must specify the message(s) to send!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val messages = config.getStrings("messages", "message")
            .map { it.replace("%player%", player.name) }
            .formatEco(config.toPlaceholderContext(data))
            .dropLastWhile { it.isEmpty() }

        val actionBar = config.getBool("action_bar")

        if (actionBar) {
            player.asAudience().sendActionBar(messages.first().toComponent())
        } else {
            for (s in messages) {
                player.asAudience().sendMessage(s.toComponent())
            }
        }

        return true
    }
}
