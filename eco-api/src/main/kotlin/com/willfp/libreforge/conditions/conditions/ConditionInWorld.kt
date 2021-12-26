package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionInWorld: Condition("in_world") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.world.name.equals(config.getString("world"), ignoreCase = true)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getStringOrNull("world")
            ?: violations.add(
                ConfigViolation(
                    "world",
                    "You must specify the world name!"
                )
            )

        return violations
    }
}