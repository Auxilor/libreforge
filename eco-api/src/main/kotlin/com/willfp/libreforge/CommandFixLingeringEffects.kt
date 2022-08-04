package com.willfp.libreforge

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.libreforge.effects.Effects
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class CommandFixLingeringEffects(
    plugin: LibReforgePlugin,
    permission: String
) : Subcommand(
    plugin,
    "fixlingeringeffects",
    permission,
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("requires-player"))
            return
        }

        val player = Bukkit.getPlayer(args[0])

        if (player == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
            return
        }

        for (attribute in Attribute.values()) {
            val attr = player.getAttribute(attribute) ?: continue
            for (modifier in attr.modifiers.toList()) {
                if (Effects.values().map { it.id }.contains(modifier.name)) {
                    attr.removeModifier(modifier)
                }
            }
        }

        @Suppress("DEPRECATION")
        player.kickPlayer("Effects repaired! You can re-log.")

        sender.sendMessage(plugin.langYml.getMessage("repaired-effects").replace("%player%", player.name))
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.isEmpty()) {
            // Currently, this case is not ever reached
            return Bukkit.getOnlinePlayers().map { it.name }
        }

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Bukkit.getOnlinePlayers().map { it.name },
                completions
            )
            return completions
        }

        return emptyList()
    }
}
