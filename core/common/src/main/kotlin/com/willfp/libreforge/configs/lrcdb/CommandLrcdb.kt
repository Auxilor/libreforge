package com.willfp.libreforge.configs.lrcdb

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.libreforge.plugin
import org.bukkit.command.CommandSender

object CommandLrcdb : PluginCommand(
    plugin,
    "lrcdb",
    "libreforge.command.lrcdb",
    false
) {
    init {
        this.addSubcommand(CommandLrcdbExport)
            .addSubcommand(CommandLrcdbImport)
    }

    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
    }
}
