package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.getDouble
import org.bukkit.entity.Player


class ConditionPlaceholderGreaterThan : Condition("placeholder_greater_than") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        val value =
            PlaceholderManager.translatePlaceholders(config.getString("placeholder"), player).toDoubleOrNull() ?: 0.0
        return value >= config.getDouble("value", player)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getStringOrNull("placeholder")
            ?: violations.add(
                ConfigViolation(
                    "placeholder",
                    "You must specify the placeholder!"
                )
            )

        if (!config.has("value")) violations.add(
            ConfigViolation(
                "value",
                "You must specify the value!"
            )
        )

        return violations
    }
}