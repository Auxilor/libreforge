package com.willfp.libreforge.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.libreforge.plugin
import org.bukkit.command.CommandSender

object CommandLibreforge : PluginCommand(
    plugin,
    "libreforge",
    "libreforge.command.libreforge",
    false
) {
    init {
        this.addSubcommand(CommandReload)
            .addSubcommand(CommandPoints)
            .addSubcommand(CommandTrigger)
            .addSubcommand(CommandDebug)
    }

    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        @Suppress("UsagesOfObsoleteApi")
        sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
    }
}
