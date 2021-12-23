package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionBelowY: Condition("below_y") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.location.y < config.getDouble("y")
    }

    override fun validateConfig(config: Config): List<com.willfp.libreforge.ConfigViolation> {
        val violations = mutableListOf<com.willfp.libreforge.ConfigViolation>()

        config.getDoubleOrNull("y")
            ?: violations.add(
                com.willfp.libreforge.ConfigViolation(
                    "y",
                    "You must specify the y level!"
                )
            )

        return violations
    }
}