package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.translatePlaceholders
import com.willfp.libreforge.*
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object EffectRunCommand : Effect<NoCompileData>("run_command") {
    override val isPermanent = false

    override val arguments = arguments {
        require(listOf("commands", "command"), "You must specify the command(s) to run!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player
        val victim = data.victim as? Player

        val commands = config.getStrings("commands", "command")
            .map {
                it.replace("%player%", player?.name ?: "%player")
                it.replace("%victim%", victim?.name ?: "")
            }
            .map { it.translatePlaceholders(config.toPlaceholderContext(data)) }
            .dropLastWhile { it.isEmpty() }

        val runnable = Runnable {
            commands.forEach {
                Bukkit.getServer().dispatchCommand(
                    Bukkit.getConsoleSender(),
                    it
                )
            }
        }
        if (Prerequisite.HAS_FOLIA.isMet)
            plugin.scheduler.runTask(runnable)
        else
            runnable.run()

        return true
    }
}
