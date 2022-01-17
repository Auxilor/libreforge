package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.getPoints
import org.bukkit.entity.Player

class ConditionPointsEqual : Condition("points_equal") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.getPoints(config.getString("type")) == config.getDoubleFromExpression("amount")
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("type")) violations.add(
            ConfigViolation(
                "type",
                "You must specify the points type!"
            )
        )

        if (!config.has("amount")) violations.add(
            ConfigViolation(
                "amount",
                "You must specify the amount of points!"
            )
        )

        return violations
    }
}