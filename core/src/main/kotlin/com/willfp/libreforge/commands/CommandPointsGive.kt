package com.willfp.libreforge.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.toNiceString
import com.willfp.libreforge.points
import com.willfp.libreforge.toFriendlyPointName
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

@Suppress("UsagesOfObsoleteApi")
internal class CommandPointsGive(plugin: EcoPlugin): Subcommand(
    plugin,
    "give",
    "libreforge.command.points.give",
    false
) {
    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        val playerString = args.getOrNull(0)

        if (playerString == null) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-player"))
            return
        }

        val player = Bukkit.getPlayer(playerString)

        if (player == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
            return
        }

        val pointString = args.getOrNull(1)

        if (pointString == null) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-point"))
            return
        }

        val amount = args.getOrNull(2)

        if (amount == null) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-amount"))
            return
        }

        val amountNum = amount.toDoubleOrNull()

        if (amountNum == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-amount"))
            return
        }

        player.points[pointString] = player.points[pointString] + amountNum

        sender.sendMessage(plugin.langYml.getMessage("points-given")
            .replace("%playername%", player.name)
            .replace("%point%", pointString.toFriendlyPointName())
            .replace("%amount%", amountNum.toNiceString())
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        return when(args.size) {
            1 -> StringUtil.copyPartialMatches(
                args[0],
                Bukkit.getOnlinePlayers().map { it.name },
                mutableListOf()
            )
            2 -> listOf("point")
            3 -> mutableListOf(
                "1",
                "5",
                "10",
                "100",
                "1000"
            )
            else -> mutableListOf()
        }
    }
}