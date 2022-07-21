package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Bukkit
import org.bukkit.Location
import kotlin.math.max
import kotlin.math.min

class EffectTraceback : Effect(
    "traceback",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    private val key = "${plugin.name}_tracekabck"

    init {
        plugin.scheduler.runTimer(20, 20) {
            for (player in Bukkit.getOnlinePlayers()) {
                @Suppress("UNCHECKED_CAST")
                val times = player.getMetadata(key).getOrNull(0) as? List<Location> ?: emptyList()
                val newTimes = listOf(player.location) + times.chunked(29)[0]
                player.removeMetadata(key, plugin)
                player.setMetadata(key, plugin.metadataValueFactory.create(newTimes))
            }
        }
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val time = max(1.0, min(30.0, config.getDoubleFromExpression("seconds", player)))
        val index = time.toInt() - 1

        @Suppress("UNCHECKED_CAST")
        val times = player.getMetadata(key).getOrNull(0) as? List<Location> ?: emptyList()
        val location = times.getOrElse(index) { times.lastOrNull() } ?: return

        player.teleport(location)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("seconds")) violations.add(
            ConfigViolation(
                "seconds",
                "You must specify the amount of seconds back in time (between 1 and 30)!"
            )
        )

        return violations
    }
}
