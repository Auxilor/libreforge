package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionInWorld: Condition("in_world") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.world.name.equals(config.getString("world"), ignoreCase = true)
    }

    override fun validateConfig(config: Config): List<com.willfp.libreforge.ConfigViolation> {
        val violations = mutableListOf<com.willfp.libreforge.ConfigViolation>()

        config.getStringOrNull("world")
            ?: violations.add(
                com.willfp.libreforge.ConfigViolation(
                    "world",
                    "You must specify the world name!"
                )
            )

        return violations
    }
}