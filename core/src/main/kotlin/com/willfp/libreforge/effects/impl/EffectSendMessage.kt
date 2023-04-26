package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSendMessage : Effect<NoCompileData>("send_message") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("message", "You must specify the message to send!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val message = config.getString("message")
            .replace("%player%", player.name)
            .formatEco(config.toPlaceholderContext(data))

        val actionBar = config.getBool("action_bar")

        if (actionBar) {
            PlayerUtils.getAudience(player)
                .sendActionBar(StringUtils.toComponent(message))
        } else {
            PlayerUtils.getAudience(player)
                .sendMessage(StringUtils.toComponent(message))
        }

        return true
    }
}
