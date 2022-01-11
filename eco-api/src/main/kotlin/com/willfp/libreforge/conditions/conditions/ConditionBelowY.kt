package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.getDouble
import org.bukkit.entity.Player

class ConditionBelowY : Condition("below_y") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.location.y < config.getDouble("y", player)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("y")) violations.add(
            ConfigViolation(
                "y",
                "You must specify the y level!"
            )
        )

        return violations
    }
}