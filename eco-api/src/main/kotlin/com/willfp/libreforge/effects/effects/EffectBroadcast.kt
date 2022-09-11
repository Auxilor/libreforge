package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Bukkit

class EffectBroadcast : Effect(
    "broadcast",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val message = config.getString("message")
            .replace("%player%", player.name)
            .let { PlaceholderManager.translatePlaceholders(it, player, config) }
            .formatEco(player)

        @Suppress("DEPRECATION")
        Bukkit.getServer().broadcastMessage(message)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("message")) violations.add(
            ConfigViolation(
                "message",
                "You must specify the message to send!"
            )
        )

        return violations
    }
}
