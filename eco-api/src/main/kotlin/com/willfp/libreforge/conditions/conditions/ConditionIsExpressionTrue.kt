package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionIsExpressionTrue : Condition("is_expression_true") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return NumberUtils.evaluateExpression(
            config.getString("expression").replace("==", "="),
            player,
            config
        ) == 1.0
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("expression")) violations.add(
            ConfigViolation(
                "expression",
                "You must specify the expression!"
            )
        )

        return violations
    }
}
