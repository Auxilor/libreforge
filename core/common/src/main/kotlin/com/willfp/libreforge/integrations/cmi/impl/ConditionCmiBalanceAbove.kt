package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.CMI
import com.Zrips.CMI.events.CMIUserBalanceChangeEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object ConditionCmiBalanceAbove : Condition<NoCompileData>("cmi_balance_above") {
    override val description = "Passes when the player's CMI balance is above a specified amount."
    override val categories = setOf("economy", "player")
    override val additionalInfo = listOf(
        "Requires the CMI plugin."
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of money!",
            description = "The amount of money the player's balance must exceed. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "world",
            description = "The world to read the balance from, for per-world economies. " +
                    "Defaults to the player's global balance.",
            type = ArgType.STRING
        )
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun handle(event: CMIUserBalanceChangeEvent) {
        event.user?.player?.toDispatcher()?.updateEffects()
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val user = CMI.getInstance()?.playerManager?.getUser(player) ?: return false

        val world = config.getStringOrNull("world")
        val balance = (if (world.isNullOrEmpty()) user.balance else user.getBalance(world)) ?: return false

        return balance > config.getDoubleFromExpression("amount", player)
    }
}
