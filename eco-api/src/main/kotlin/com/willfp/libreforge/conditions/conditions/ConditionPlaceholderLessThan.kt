package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player


class ConditionPlaceholderLessThan : Condition("placeholder_less_than") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        val value = PlaceholderManager.translatePlaceholders(config.getString("placeholder"), player).toDoubleOrNull() ?: 0.0
        return value < config.getDouble("value")
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

        config.getDoubleOrNull("value")
            ?: violations.add(
                ConfigViolation(
                    "value",
                    "You must specify the value!"
                )
            )

        return violations
    }
}