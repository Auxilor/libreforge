package com.willfp.libreforge.integrations.lands.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.plugin
import me.angeschossen.lands.api.LandsIntegration
import org.bukkit.entity.Player

object ConditionLandsBalanceAbove : Condition<NoCompileData>("lands_balance_above") {
    override val description = "Passes when the Lands area at the player's location has a balance above the specified amount."
    override val categories = setOf("world", "economy")
    override val additionalInfo = listOf("Requires the Lands plugin.")

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount!",
            description = "The balance threshold the land's balance must exceed.",
            type = ArgType.EXPRESSION
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val location = dispatcher.location ?: return false
        val area = LandsIntegration.of(plugin).getArea(location) ?: return false

        val balance = area.land.balance
        val required = config.getDoubleFromExpression("amount", player)

        return balance > required
    }
}
