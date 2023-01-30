package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionAboveBalance : Condition<NoCompileData>("above_balance") {
    override val arguments = arguments {
        require("balance", "You must specify the minimum balance!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return EconomyManager.hasAmount(player, config.getDoubleFromExpression("balance", player))
    }
}
