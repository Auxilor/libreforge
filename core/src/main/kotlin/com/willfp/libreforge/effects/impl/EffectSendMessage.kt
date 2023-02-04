package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectSendMessage : Effect(
    "send_message",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("message", "You must specify the message to send!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val message = config.getString("message")
            .replace("%player%", player.name)
            .let { PlaceholderManager.translatePlaceholders(it, player, config) }
            .formatEco(player)

        val actionBar = config.getBool("action_bar")

        if (actionBar) {
            PlayerUtils.getAudience(player)
                .sendActionBar(StringUtils.toComponent(message))
        } else {
            PlayerUtils.getAudience(player)
                .sendMessage(StringUtils.toComponent(message))
        }
    }
}
