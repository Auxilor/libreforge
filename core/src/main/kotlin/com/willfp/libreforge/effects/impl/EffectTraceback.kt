package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.Location

object EffectTraceback : Effect<NoCompileData>("traceback") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("seconds", "You must specify the amount of seconds to go back in time (1-30)!")
    }

    private val key = "libreforge_traceback"

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val time = config.getDoubleFromExpression("seconds", data).toInt().coerceIn(1..30)

        @Suppress("UNCHECKED_CAST")
        val times = player.getMetadata(key).getOrNull(0)?.value() as? List<Location> ?: emptyList()

        // Most recent is last
        val index = times.size - time

        val location = times.getOrElse(index) { times.lastOrNull() } ?: return false

        player.teleport(location)

        return true
    }

    init {
        plugin.scheduler.runTimer(20, 20) {
            for (player in Bukkit.getOnlinePlayers()) {
                @Suppress("UNCHECKED_CAST")
                val times = player.getMetadata(key).getOrNull(0)?.value() as? List<Location> ?: emptyList()
                val newTimes = (if (times.size < 29) times else times.drop(1)) + player.location

                player.removeMetadata(key, plugin)
                player.setMetadata(key, plugin.metadataValueFactory.create(newTimes))
            }
        }
    }
}
