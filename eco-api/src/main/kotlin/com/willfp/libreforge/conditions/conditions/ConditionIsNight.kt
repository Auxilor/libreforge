package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionIsNight : Condition("is_night") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        val isNight = player.world.time < 12300 || player.world.time > 23850

        return isNight == config.getBool("is_night")
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("is_night")) violations.add(
            ConfigViolation(
                "is_night",
                "You must specify if the time must be night or not!"
            )
        )

        return violations
    }
}