package com.willfp.libreforge.lrcdb

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.libreforge.LibReforgePlugin
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import java.util.concurrent.TimeUnit

class CommandExport(
    plugin: LibReforgePlugin,
    private val configGetter: () -> Collection<ExportableConfig>
) : Subcommand(
    plugin,
    "export",
    "${plugin.name.lowercase()}.command.export",
    false
) {
    // Using 0 as the key
    private val cache = Caffeine.newBuilder()
        .expireAfterWrite(5, TimeUnit.SECONDS)
        .build<Int, Collection<ExportableConfig>> {
            configGetter()
        }

    private val configs: Collection<ExportableConfig>
        get() = cache[0]

    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-config-name"))
            return
        }

        val name = args[0]

        val exportable = configs.firstOrNull { it.name == name }

        if (exportable == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-config-name"))
            return
        }

        onLrcdbThread {
            val response = exportable.export(plugin as LibReforgePlugin, false)

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

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                configs.map { it.name },
                completions
            )
            return completions
        }

        return emptyList()
    }
}
