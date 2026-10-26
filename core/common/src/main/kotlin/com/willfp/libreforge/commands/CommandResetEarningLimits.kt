package com.willfp.libreforge.commands

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.libreforge.earning.EarningLimits
import com.willfp.libreforge.plugin
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import java.util.UUID

internal object CommandResetEarningLimits : Subcommand(
    plugin,
    "reset_earning_limits",
    "libreforge.command.reset_earning_limits",
    false
) {
    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        val target = args.getOrNull(0)

        if (target == null) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-player"))
            return
        }

        val id = args.getOrNull(1)

        if (id == null) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-earning-limit"))
            return
        }

        val uuid = runCatching { UUID.fromString(target) }.getOrNull()
        var player: OfflinePlayer? = Bukkit.getPlayerExact(target)

        if (player == null && uuid != null) {
            player = Bukkit.getOfflinePlayer(uuid)
        }

        if (player == null && Prerequisite.HAS_PAPER.isMet) {
            player = Bukkit.getOfflinePlayerIfCached(target)
        }

        if (player == null || (!player.isOnline && !player.hasPlayedBefore())) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
            return
        }

        if (!EarningLimits.reset(player, id)) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-earning-limit"))
            return
        }

        sender.sendMessage(
            plugin.langYml.getMessage("earning-limit-reset")
                .replace("%playername%", player.name ?: player.uniqueId.toString())
                .replace("%group%", id)
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
                EarningLimits.groupIds,
                mutableListOf()
            )

            else -> emptyList()
        }
    }
}
