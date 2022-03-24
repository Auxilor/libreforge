package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.conditions.Conditions
import org.bukkit.entity.Player

class ConditionOr: Condition("or") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return config.getSubsections("conditions").mapNotNull { Conditions.compile(it, "OR condition") }
            .any { it.condition.isConditionMet(player, it.config) }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("conditions")) violations.add(
            ConfigViolation(
                "conditions",
                "You must specify the conditions to be checked!"
            )
        )

        return violations
    }

}