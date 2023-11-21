package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionAboveBalance : Condition<NoCompileData>("above_balance") {
    override val arguments = arguments {
        require("balance", "You must specify the minimum balance!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return EconomyManager.hasAmount(player, config.getDoubleFromExpression("balance", player))
    }
}
