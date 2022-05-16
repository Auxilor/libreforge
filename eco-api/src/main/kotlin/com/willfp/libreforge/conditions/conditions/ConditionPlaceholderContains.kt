package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionPlaceholderContains : Condition("placeholder_contains") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return PlaceholderManager.translatePlaceholders(config.getString("placeholder"), player)
            .contains(config.getString("value"), ignoreCase = config.getBool("ignore_case"))
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("placeholder")) violations.add(
            ConfigViolation(
                "placeholder",
                "You must specify the placeholder!"
            )
        )

        return violations
    }
}
