package com.willfp.libreforge.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import org.bukkit.command.CommandSender

internal class CommandReload(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "reload",
    "libreforge.command.reload",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        @Suppress("UsagesOfObsoleteApi")
        sender.sendMessage(plugin.langYml.getMessage("reloaded"))
        plugin.reload(false)
    }
}
