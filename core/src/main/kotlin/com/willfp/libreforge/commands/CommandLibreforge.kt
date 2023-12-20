package com.willfp.libreforge.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.command.CommandSender

class CommandLibreforge(
    plugin: EcoPlugin
) : PluginCommand(
    plugin,
    "libreforge",
    "libreforge.command.libreforge",
    false
) {
    init {
        this.addSubcommand(CommandReload(plugin))
            .addSubcommand(CommandPoints(plugin))
            .addSubcommand(CommandTrigger(plugin))
    }

    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        @Suppress("UsagesOfObsoleteApi")
        sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
    }
}
