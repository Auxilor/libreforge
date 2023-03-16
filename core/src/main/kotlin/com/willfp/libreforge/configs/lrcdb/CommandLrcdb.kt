package com.willfp.libreforge.configs.lrcdb

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.command.CommandSender

class CommandLrcdb(plugin: EcoPlugin) : PluginCommand(
    plugin,
    "lrcdb",
    "libreforge.command.lrcdb",
    false
) {
    init {
        this.addSubcommand(CommandLrcdbExport(plugin))
            .addSubcommand(CommandLrcdbImport(plugin))
    }

    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
    }
}
