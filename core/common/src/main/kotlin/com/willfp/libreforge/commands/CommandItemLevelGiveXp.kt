package com.willfp.libreforge.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.toNiceString
import com.willfp.libreforge.levels.LevelTypes
import com.willfp.libreforge.levels.levels
import com.willfp.libreforge.plugin
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

internal object CommandItemLevelGiveXp : Subcommand(
    plugin,
    "give_xp",
    "libreforge.command.item_level.give_xp",
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

        val levelIdString = args.getOrNull(1)

        if (levelIdString == null) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-level-id"))
            return
        }

        val levelType = LevelTypes[levelIdString]

        if (levelType == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-level-id"))
            return
        }

        val amountString = args.getOrNull(2)

        if (amountString == null) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-amount"))
            return
        }

        val amount = amountString.toDoubleOrNull()

        if (amount == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-amount"))
            return
        }

        val item = player.inventory.itemInMainHand

        if (item.type.isAir) {
            sender.sendMessage(plugin.langYml.getMessage("must-have-item"))
            return
        }

        item.levels.gainXP(levelType, amount, PlaceholderContext(player))

        sender.sendMessage(
            plugin.langYml.getMessage("item-level-xp-given")
                .replace("%playername%", player.name)
                .replace("%level%", levelIdString)
                .replace("%amount%", amount.toNiceString())
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> StringUtil.copyPartialMatches(
                args[0],
                Bukkit.getOnlinePlayers().map { it.name },
                mutableListOf()
            )

            2 -> StringUtil.copyPartialMatches(
                args[1],
                LevelTypes.values().map { it.id },
                mutableListOf()
            )

            3 -> mutableListOf("1", "5", "10", "100", "1000")

            else -> mutableListOf()
        }
    }
}
