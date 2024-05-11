package com.willfp.libreforge.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.libreforge.Plugins
import com.willfp.libreforge.activeEffects
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.holders
import com.willfp.libreforge.mutators.Mutators
import com.willfp.libreforge.points
import com.willfp.libreforge.slot.SlotTypes
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal class CommandDebug(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "debug",
    "libreforge.command.debug",
    false
) {
    @Suppress("DEPRECATION")
    override fun onExecute(sender: CommandSender, args: List<String>) {
        plugin.logger.info("Running version ${plugin.description.version} on ${plugin.server.version}.")

        plugin.logger.info("Loaded ${Effects.values().size} effects:")
        plugin.logger.info(Effects.values().joinToString(", ") { it.id })

        plugin.logger.info("Loaded ${Conditions.values().size} conditions:")
        plugin.logger.info(Conditions.values().joinToString(", ") { it.id })

        plugin.logger.info("Loaded ${Triggers.values().size} triggers:")
        plugin.logger.info(Triggers.values().joinToString(", ") { it.id })

        plugin.logger.info("Loaded ${Filters.values().size} filters:")
        plugin.logger.info(Filters.values().joinToString(", ") { it.id })

        plugin.logger.info("Loaded ${Mutators.values().size} mutators:")
        plugin.logger.info(Mutators.values().joinToString(", ") { it.id })

        plugin.logger.info("Loaded ${SlotTypes.values().size} slot types:")
        plugin.logger.info(SlotTypes.values().joinToString(", ") { it.id })

        plugin.logger.info("There are ${Plugins.values().size} libreforge plugins loaded:")
        for (loadedPlugin in Plugins.values()) {
            plugin.logger.info("- ${loadedPlugin.plugin.name} v${loadedPlugin.plugin.description.version} " +
                    "[${loadedPlugin.id}] (${loadedPlugin.categories.values().joinToString(", ") { it.id }})")
        }

        if (sender is Player) {
            plugin.logger.info("Player holders:")
            plugin.logger.info(sender.toDispatcher().holders.joinToString(", ") { it.holder.id.toString() })

            plugin.logger.info("Player active effects:")
            plugin.logger.info(sender.toDispatcher().activeEffects.flatMap { it.effects.map { it.effect.id } }.joinToString(", "))

            plugin.logger.info("Player points:")
            plugin.logger.info(sender.points.toString())
        }

        @Suppress("UsagesOfObsoleteApi")
        sender.sendMessage(plugin.langYml.getMessage("debug"))
    }
}
