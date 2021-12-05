package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player


class ConditionPlaceholderEquals : Condition("placeholder_equals") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return PlaceholderManager.translatePlaceholders(config.getString("placeholder", false), player)
            .equals(config.getString("value"), ignoreCase = true)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getStringOrNull("placeholder", false)
            ?: violations.add(
                ConfigViolation(
                    "placeholder",
                    "You must specify the placeholder!"
                )
            )

        config.getStringOrNull("value", false)
            ?: violations.add(
                ConfigViolation(
                    "value",
                    "You must specify the value!"
                )
            )

        return violations
    }
}