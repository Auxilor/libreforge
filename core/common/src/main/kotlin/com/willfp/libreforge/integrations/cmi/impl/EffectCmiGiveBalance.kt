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

object EffectCmiGiveBalance : Effect<NoCompileData>("cmi_give_balance") {
    override val description = "Adds money to the player's CMI balance."
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
            "You must specify the amount of money to give!",
            description = "The amount of money to add to the player's balance. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val user = CMI.getInstance()?.playerManager?.getUser(player) ?: return false

        user.deposit(config.getDoubleFromExpression("amount", data))

        return true
    }
}
