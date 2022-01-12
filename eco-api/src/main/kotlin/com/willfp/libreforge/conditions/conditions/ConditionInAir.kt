package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionInAir : Condition("in_air") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.location.block.isEmpty == config.getBool("in_air")
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("in_air")) violations.add(
            ConfigViolation(
                "in_air",
                "You must specify if the player must be in air on on land!"
            )
        )

        return violations
    }
}