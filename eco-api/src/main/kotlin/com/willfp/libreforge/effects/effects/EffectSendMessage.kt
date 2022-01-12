package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectSendMessage : Effect(
    "send_message",
    supportsFilters = true,
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val message = config.getFormattedString("message")
            .replace("%player%", player.name)

        val actionBar = config.getBool("action_bar")

        if (actionBar) {
            PlayerUtils.getAudience(player)
                .sendActionBar(StringUtils.toComponent(message))
        } else {
            PlayerUtils.getAudience(player)
                .sendMessage(StringUtils.toComponent(message))
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("message")) violations.add(
            ConfigViolation(
                "message",
                "You must specify the message to send!"
            )
        )

        if (!config.has("action_bar")) violations.add(
            ConfigViolation(
                "action_bar",
                "You must specify if to send the message to the action bar!"
            )
        )

        return violations
    }
}