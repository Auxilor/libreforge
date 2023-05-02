package com.willfp.libreforge.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import org.bukkit.command.CommandSender

class CommandReloadExtensions(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "reloadextensions",
    "libreforge.command.reloadextensions",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(plugin.langYml.getMessage("reloaded"))
        for (extension in plugin.extensionLoader.loadedExtensions) {
            extension.handleReload()
        }
    }
}
