package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionAboveBalance: Condition("above_balance") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return EconomyManager.hasAmount(player, config.getDouble("balance"))
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getDoubleOrNull("balance")
            ?: violations.add(
                ConfigViolation(
                    "balance",
                    "You must specify the minimum balance!"
                )
            )

        return violations
    }
}