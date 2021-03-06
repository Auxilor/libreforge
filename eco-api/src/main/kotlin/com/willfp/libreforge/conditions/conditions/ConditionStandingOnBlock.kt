package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionStandingOnBlock : Condition("standing_on_block") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.world.getBlockAt(player.location.clone().add(0.0, -1.0, 0.0))
            .type.name.equals(config.getString("block"), ignoreCase = true)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("block")) violations.add(
            ConfigViolation(
                "block",
                "You must specify the type of block the player must be standing on!"
            )
        )

        return violations
    }
}