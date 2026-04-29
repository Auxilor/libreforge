package com.willfp.libreforge.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.toNiceString
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.impl.TriggerTakeDamage
import org.bukkit.command.CommandSender

internal object CommandReload : Subcommand(
    plugin,
    "reload",
    "libreforge.command.reload",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(
            plugin.langYml.getMessage("reloaded", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%time%", plugin.reloadWithTime(false).toNiceString())
        )
        TriggerTakeDamage.notifyOfEntityDamageChange(sender = sender)
    }
}
