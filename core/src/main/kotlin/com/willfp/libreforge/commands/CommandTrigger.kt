package com.willfp.libreforge.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.GlobalDispatcher
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.impl.TriggerGroupCustom
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.UUID

internal class CommandTrigger(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "trigger",
    "libreforge.command.trigger",
    false
) {
    @Suppress("UsagesOfObsoleteApi")
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val dispatcherName = args.getOrNull(0)

        if (dispatcherName == null) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-dispatcher"))
            return
        }

        val dispatcherUUID = runCatching { UUID.fromString(dispatcherName) }.getOrNull()

        val dispatchers = mutableListOf<Dispatcher<*>>()

        if (dispatcherName.lowercase() in setOf("global", "server")) {
            dispatchers += GlobalDispatcher
        }

        if (dispatcherName.lowercase() in setOf("all", "everyone")) {
            dispatchers += Bukkit.getOnlinePlayers().map { it.toDispatcher() }
        }

        Bukkit.getPlayer(dispatcherName)?.toDispatcher()?.let { dispatchers += it }

        if (dispatcherUUID != null) {
            Bukkit.getEntity(dispatcherUUID)?.toDispatcher()?.let { dispatchers += it }
        }

        if (dispatchers.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-dispatcher"))
            return
        }

        val trigger = args.getOrNull(1)

        if (trigger == null) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-trigger"))
            return
        }

        val value = args.getOrNull(2)?.toDoubleOrNull()

        for (dispatcher in dispatchers) {
            TriggerGroupCustom.create(trigger).dispatch(
                dispatcher,
                TriggerData(
                    player = dispatcher.get(),
                    victim = dispatcher.get(),
                    location = dispatcher.get<LivingEntity>()?.location,
                    value = value ?: 1.0
                )
            )
        }

        sender.sendMessage(plugin.langYml.getMessage("triggered").replace("%id%", trigger))
        plugin.reload(false)
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        return when(args.size) {
            1 -> StringUtil.copyPartialMatches(
                args[0],
                listOf("global", "server", "all", "everyone") + Bukkit.getOnlinePlayers().map { it.name },
                mutableListOf()
            )
            2 -> StringUtil.copyPartialMatches(
                args[1],
                TriggerGroupCustom.knownTriggers + "mycustomtrigger",
                mutableListOf()
            )
            else -> StringUtil.copyPartialMatches(
                args[2],
                listOf("0.5", "1", "2", "5"),
                mutableListOf()
            )
        }
    }
}
