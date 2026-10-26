package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.economy.balance
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.earning.EarningLimitGroup
import com.willfp.libreforge.earning.EarningLimits
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGiveMoney : Effect<EarningLimitGroup?>("give_money") {
    override val description = "Deposits money into the player's economy balance when triggered."
    override val categories = setOf("economy")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of money to give!",
            description = "The amount of money to deposit into the player's balance. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 10"
        )
        optional(
            "earning-limit",
            description = "The ID of the earning-limit group.",
            type = ArgType.STRING
        )
    }

    override fun makeCompileData(config: Config, context: ViolationContext): EarningLimitGroup? {
        return EarningLimits.validateGroup(config, context)
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: EarningLimitGroup?): Boolean {
        val player = data.player ?: return false

        var amount = config.getDoubleFromExpression("amount", data)

        if (compileData != null) {
            amount = minOf(amount, EarningLimits.getRemaining(player, compileData))
            EarningLimits.addEarned(player, compileData, amount)
        }

        player.balance += amount

        return true
    }
}
