package com.willfp.libreforge.configs.lrcdb

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.commands.notifyNull
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.readConfig
import com.willfp.libreforge.Plugins
import com.willfp.libreforge.configs.onLrcdbThread
import org.bukkit.command.CommandSender
import java.io.BufferedReader
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

class CommandLrcdbImport(plugin: EcoPlugin) : Subcommand(
    plugin,
    "import",
    "libreforge.command.lrcdb",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-lrcdb-id"))
            return
        }

        val id = args[0]

        onLrcdbThread {
            val url = URI("https://lrcdb.auxilor.io/api/v1/getConfigByID?id=$id&isDownload=true").toURL()
            val connection = url.openConnection() as HttpURLConnection
            val code = connection.responseCode

            val isError = code in 400..599

            val reader = if (isError) {
                connection.errorStream.reader()
            } else {
                connection.inputStream.reader()
            }

            val res = readConfig(BufferedReader(reader).readText(), ConfigType.JSON)

            if (isError) {
                sender.sendMessage(
                    plugin.langYml.getMessage("lrcdb-import-error")
                        .replace(
                            "%message%",
                            res.getStringOrNull("message") ?: "HTTP Error $code"
                        )
                )
            } else {
                val config = res.getSubsection("config")

                val pluginName = config.getString("plugin")
                val categoryID = config.getString("category")

                val name = config.getString("name")
                val contents = config.getString("contents")

                sender.sendMessage(
                    plugin.langYml.getMessage("lrcdb-import-success")
                        .replace("%name%", name)
                )

                val plugin = Plugins[pluginName].notifyNull("plugin-not-installed") ?: return@onLrcdbThread
                val directory = plugin.categories[categoryID]!!.directory

                val dir = File(plugin.dataFolder, "$directory/imports")
                dir.mkdirs()
                val file = File(dir, "$name.yml")
                file.writeText(contents)
            }
        }
    }
}
