package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionAboveBalance : Condition("above_balance") {
    override val arguments = arguments {
        require("balance", "You must specify the minimum balance!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return EconomyManager.hasAmount(player, config.getDoubleFromExpression("balance", player))
    }
}