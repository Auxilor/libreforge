package com.willfp.libreforge.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.libreforge.plugin
import org.bukkit.command.CommandSender

internal object CommandItemLevel : Subcommand(
    plugin,
    "item_level",
    "libreforge.command.item_level",
    false
) {
    init {
        this.addSubcommand(CommandItemLevelGiveXp)
            .addSubcommand(CommandItemLevelGiveLevel)
    }

    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
    }
}
