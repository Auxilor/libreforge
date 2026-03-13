package com.willfp.libreforge.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.libreforge.plugin
import org.bukkit.command.CommandSender

internal class CommandEmpty(
    label: String,
) : PluginCommand(
    plugin,
    label,
    "libreforge.command.empty.$label",
    true
) {
    override fun onExecute(sender: CommandSender, args: MutableList<String>) {}
}
