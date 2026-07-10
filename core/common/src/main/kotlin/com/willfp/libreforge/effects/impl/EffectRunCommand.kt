package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.translatePlaceholders
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object EffectRunCommand : Effect<NoCompileData>("run_command") {
    override val description = "Runs one or more commands as the console when triggered."
    override val categories = setOf("chat")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            listOf("commands", "command"),
            "You must specify the command(s) to run!",
            description = "The command or list of commands to run as console. Use %player% and %victim% as placeholders.",
            type = ArgType.STRING_LIST,
            example = listOf("say %player% used an ability!", "effect give %player% speed 5 1")
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player
        val victim = data.victim as? Player

        val commands = config.getStrings("commands", "command")
            .map { it.replace("%player%", player?.name ?: "%player")
            it.replace("%victim%", victim?.name ?: "")}
            .map { it.translatePlaceholders(config.toPlaceholderContext(data)) }
            .dropLastWhile { it.isEmpty() }

        commands.forEach {
            Bukkit.getServer().dispatchCommand(
                Bukkit.getConsoleSender(),
                it
            )
        }

        return true
    }
}
