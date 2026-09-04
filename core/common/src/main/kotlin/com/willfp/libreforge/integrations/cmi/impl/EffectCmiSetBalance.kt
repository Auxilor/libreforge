package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.CMI
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import kotlin.math.absoluteValue

object EffectCmiSetBalance : Effect<NoCompileData>("cmi_set_balance") {
    override val description = "Sets the player's CMI balance to a specified amount."
    override val categories = setOf("economy")
    override val additionalInfo = listOf(
        "Requires the CMI plugin."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of money!",
            description = "The amount to set the player's balance to. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val user = CMI.getInstance()?.playerManager?.getUser(player) ?: return false
        val balance = user.balance ?: return false

        val delta = config.getDoubleFromExpression("amount", data) - balance

        if (delta > 0) {
            user.deposit(delta)
        } else if (delta < 0) {
            user.withdraw(delta.absoluteValue)
        }

        return true
    }
}
