package com.willfp.libreforge.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.toNiceString
import com.willfp.libreforge.globalPoints
import com.willfp.libreforge.points
import com.willfp.libreforge.toFriendlyPointName
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

@Suppress("UsagesOfObsoleteApi")
internal class CommandPointsReset(plugin: EcoPlugin) : Subcommand(
    plugin,
    "reset",
    "libreforge.command.points.reset",
    false
) {
    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        val targetString = args.getOrNull(0)

        if (targetString == null) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-player"))
            return
        }

        val pointString = args.getOrNull(1)
        if (pointString == null) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-point"))
            return
        }

        val resetAmount = 0.0

        if (targetString.equals("global", ignoreCase = true)) {
            // 🔒 Leave global logic exactly as-is
            globalPoints[pointString] = resetAmount

            sender.sendMessage(plugin.langYml.getMessage("points-reset")
                .replace("%playername%", "server")
                .replace("%point%", pointString.toFriendlyPointName())
                .replace("%amount%", resetAmount.toNiceString())
            )
            return
        }

        if (targetString == "*") {
            // Reset for ALL online players
            Bukkit.getOnlinePlayers().forEach { it.points[pointString] = resetAmount }

            sender.sendMessage(plugin.langYml.getMessage("points-reset-all")
                .replace("%point%", pointString.toFriendlyPointName())
                .replace("%amount%", resetAmount.toNiceString())
            )
            return
        }

        val player = Bukkit.getPlayer(targetString)
        if (player == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
            return
        }

        player.points[pointString] = resetAmount

        sender.sendMessage(plugin.langYml.getMessage("points-reset")
            .replace("%playername%", player.name)
            .replace("%point%", pointString.toFriendlyPointName())
            .replace("%amount%", resetAmount.toNiceString())
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> {
                val candidates = Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
                candidates.add("global")
                candidates.add("*")
                StringUtil.copyPartialMatches(args[0], candidates, mutableListOf())
            }
            2 -> listOf("point")
            else -> mutableListOf()
        }
    }
}
