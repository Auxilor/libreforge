package com.willfp.libreforge.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import org.bukkit.command.CommandSender

@Suppress("UsagesOfObsoleteApi")
internal class CommandPoints(plugin: EcoPlugin): Subcommand(
    plugin,
    "points",
    "libreforge.command.points",
    false
) {
    init {
        this.addSubcommand(CommandPointsGive(plugin))
            .addSubcommand(CommandPointsTake(plugin))
            .addSubcommand(CommandPointsSet(plugin))
            .addSubcommand(CommandPointsGet(plugin))
    }

    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
    }
}