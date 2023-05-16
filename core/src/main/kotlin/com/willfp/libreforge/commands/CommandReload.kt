package com.willfp.libreforge.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import org.bukkit.command.CommandSender

class CommandReload(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "reload",
    "libreforge.command.reload",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(plugin.langYml.getMessage("reloaded"))
        plugin.reload(false)
    }
}
