package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class EffectRunCommand : Effect(
    "run_command",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("command", "You must specify the command to run!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val victim = data.victim as? Player

        var command = config.getString("command")
            .replace("%player%", player.name)

        if (victim != null) {
            command = command.replace("%victim%", victim.name)
        }

        command = PlaceholderManager.translatePlaceholders(command, player, config)

        Bukkit.getServer().dispatchCommand(
            Bukkit.getConsoleSender(),
            command
        )
    }
}
