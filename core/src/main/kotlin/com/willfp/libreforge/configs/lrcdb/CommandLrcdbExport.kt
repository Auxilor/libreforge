package com.willfp.libreforge.configs.lrcdb

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.commands.notifyNull
import com.willfp.libreforge.Plugins
import com.willfp.libreforge.configs.onLrcdbThread
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class CommandLrcdbExport(plugin: EcoPlugin) : Subcommand(
    plugin,
    "export",
    "libreforge.command.lrcdb",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val pluginName = args.getOrNull(0).notifyNull("must-specify-plugin") ?: return
        val foundPlugin = Plugins[pluginName.lowercase()].notifyNull("invalid-plugin") ?: return

        val categoryName = args.getOrNull(1).notifyNull("must-specify-category") ?: return
        val category = foundPlugin.categories.values().firstOrNull { it.id == categoryName }.notifyNull("invalid-category") ?: return

        val configName = args.getOrNull(2).notifyNull("must-specify-config-name") ?: return
        val config = category[configName].notifyNull("invalid-config-name") ?: return

        onLrcdbThread {
            val response = config.share(false)

            if (response.success) {
                sender.sendMessage(
                    plugin.langYml.getMessage("lrcdb-export-success")
                        .replace("%name%", name)
                        .replace("%id%", response.body.getString("id"))
                )
            } else {
                sender.sendMessage(
                    plugin.langYml.getMessage("lrcdb-export-error")
                        .replace(
                            "%message%",
                            response.body.getStringOrNull("message") ?: "HTTP Error ${response.code}"
                        )
                )
            }
        }
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.isEmpty()) {
            return emptyList()
        }

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Plugins.values().map { it.id },
                completions
            )
            return completions
        }

        val plugin = Plugins[args[0]] ?: return emptyList()

        if (args.size == 2) {
            StringUtil.copyPartialMatches(
                args[1],
                plugin.categories.values().map { it.id },
                completions
            )
            return completions
        }

        val category = plugin.categories[args[1]] ?: return emptyList()

        if (args.size == 3) {
            StringUtil.copyPartialMatches(
                args[1],
                category.values().map { it.id },
                completions
            )
            return completions
        }

        return emptyList()
    }
}
