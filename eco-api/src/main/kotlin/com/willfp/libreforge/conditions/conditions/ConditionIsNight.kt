package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerMoveEvent

class ConditionIsNight: Condition("is_night") {
    override fun isConditionMet(player: Player, config: JSONConfig): Boolean {
        val isNight = player.world.time < 12300 || player.world.time > 23850

        return isNight == config.getBool("is_night")
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getBoolOrNull("is_night")
            ?: violations.add(
                ConfigViolation(
                    "is_night",
                    "You must specify if the time must be night or not!"
                )
            )

        return violations
    }
}