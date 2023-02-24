package com.willfp.libreforge.lrcdb

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand

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
}
