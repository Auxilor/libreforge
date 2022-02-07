package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.Player

class EffectRunPlayerCommand : Effect(
    "run_player_command",
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val victim = data.victim as? Player

        var command = config.getString("command")
            .replace("%player%", player.name)

        if (victim != null) {
            command = command.replace("%victim%", victim.name)
        }

        val isOp = player.isOp

        try {
            if (!isOp) {
                player.isOp = config.getBool("as_op")
            }
            player.performCommand(command)
        } finally {
            player.isOp = isOp
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("command")) violations.add(
            ConfigViolation(
                "command",
                "You must specify the command to execute!"
            )
        )

        if (!config.has("as_op")) violations.add(
            ConfigViolation(
                "as_op",
                "You must specify if the command should be ran as op!"
            )
        )

        return violations
    }
}