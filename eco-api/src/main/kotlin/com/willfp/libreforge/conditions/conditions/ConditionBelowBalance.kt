package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionBelowBalance : Condition("below_balance") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return !EconomyManager.hasAmount(player, config.getDoubleFromExpression("balance", player))
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("balance")) violations.add(
            ConfigViolation(
                "balance",
                "You must specify the maximum balance!"
            )
        )

        return violations
    }
}