package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.economy.balance
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectTakeMoney : Effect<NoCompileData>("take_money") {
    override val description = "Removes a specified amount of money from the player's balance."
    override val categories = setOf("economy")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of money to take!",
            description = "The amount of money to remove from the player. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        player.balance -= config.getDoubleFromExpression("amount", data)

        return true
    }
}
