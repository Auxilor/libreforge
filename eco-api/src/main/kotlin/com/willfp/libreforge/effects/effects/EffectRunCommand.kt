package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class EffectRunCommand : Effect(
    "run_command",
    supportsFilters = true,
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: JSONConfig) {
        val player = data.player ?: return
        val victim = data.victim as? Player

        var command = config.getString("command", false)
            .replace("%player%", player.name)

        if (victim != null) {
            command = command.replace("%victim%", victim.name)
        }

        Bukkit.getServer().dispatchCommand(
            Bukkit.getConsoleSender(),
            command
        )
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getStringOrNull("command")
            ?: violations.add(
                ConfigViolation(
                    "command",
                    "You must specify the command to execute!"
                )
            )

        return violations
    }
}