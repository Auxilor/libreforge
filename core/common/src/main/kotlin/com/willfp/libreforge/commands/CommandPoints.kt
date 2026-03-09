package com.willfp.libreforge.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.libreforge.plugin
import org.bukkit.command.CommandSender

internal object CommandPoints : Subcommand(
    plugin,
    "points",
    "libreforge.command.points",
    false
) {
    init {
        this.addSubcommand(CommandPointsGet)
            .addSubcommand(CommandPointsGive)
            .addSubcommand(CommandPointsReset)
            .addSubcommand(CommandPointsSet)
            .addSubcommand(CommandPointsTake)
    }

    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
    }
}