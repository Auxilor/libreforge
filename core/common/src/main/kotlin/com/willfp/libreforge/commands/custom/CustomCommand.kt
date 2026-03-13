package com.willfp.libreforge.commands.custom

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CustomCommand(
    override val id: String,
    alias: String,
    val permission: String?,
    private val plugin: EcoPlugin,
    config: Config
) : KRegistrable {
    private val parts = alias.split(" ").filter { it.isNotBlank() }

    val commandName = parts.first().lowercase()
    private val arguments = parts.drop(1)

    val playerArgIndex = arguments.indexOfFirst { it.equals("<player>", ignoreCase = true) }

    val requiredValueArgIndex = arguments.indexOfFirst { it.equals("<value>", ignoreCase = true) }
    val optionalValueArgIndex = arguments.indexOfFirst { it.equals("[value]", ignoreCase = true) }
    val valueArgIndex = when {
        requiredValueArgIndex != -1 -> requiredValueArgIndex
        optionalValueArgIndex != -1 -> optionalValueArgIndex
        else -> -1
    }

    val hasPlayerArg = playerArgIndex != -1
    val hasValueArg = valueArgIndex != -1
    val hasRequiredValueArg = requiredValueArgIndex != -1

    val commandEffects = Effects.compileChain(
        config.getSubsections("effects"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin, "Command $id effects")
    )

    val command = object : PluginCommand(
        plugin,
        commandName,
        permission ?: "",
        !hasPlayerArg
    ) {
        override fun onExecute(sender: CommandSender, args: MutableList<String>) {
            val player = commandPlayer(sender, args) ?: return
            val value = commandValue(sender, args.getOrNull(valueArgIndex)) ?: return

            commandEffects?.trigger(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    value = value
                )
            )
        }

        override fun tabComplete(sender: CommandSender, args: MutableList<String>): List<String> {
            return when {
                hasPlayerArg && args.size == playerArgIndex + 1 -> {
                    Bukkit.getOnlinePlayers()
                        .map { it.name }
                        .filter { it.startsWith(args[playerArgIndex], true) }
                }

                hasValueArg && args.size == valueArgIndex + 1 -> {
                    listOf(
                        "1",
                        "5",
                        "10",
                        "100",
                        "1000"
                    ).filter { it.startsWith(args[valueArgIndex], true) }
                }

                else -> emptyList()
            }
        }
    }

    private fun commandPlayer(sender: CommandSender, args: List<String>): Player? {
        if (!hasPlayerArg) {
            return sender as? Player
        }

        val playerString = args.getOrNull(playerArgIndex)

        if (playerString == null) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-player"))
            return null
        }

        val player = Bukkit.getPlayer(playerString)

        if (player == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
            return null
        }

        return player
    }

    private fun commandValue(sender: CommandSender, valueString: String?): Double? {
        if (valueString == null) {
            if (hasRequiredValueArg) {
                sender.sendMessage(plugin.langYml.getMessage("must-specify-amount"))
                return null
            }

            return 1.0
        }

        val value = valueString.toDoubleOrNull()

        if (value == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-amount"))
            return null
        }

        return value
    }

    override fun onRegister() {
        command.register()
    }

    override fun onRemove() {
        command.unregister()
    }
}