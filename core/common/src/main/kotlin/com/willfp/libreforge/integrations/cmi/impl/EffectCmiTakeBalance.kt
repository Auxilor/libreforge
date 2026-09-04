package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.CMI
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getOrElse
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectCmiTakeBalance : Effect<NoCompileData>("cmi_take_balance") {
    override val description = "Removes money from the player's CMI balance."
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
            "You must specify the amount of money to take!",
            description = "The amount of money to remove from the player's balance. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "require_balance",
            description = "Whether the player must be able to afford the full amount. Defaults to true.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val user = CMI.getInstance()?.playerManager?.getUser(player) ?: return false

        val amount = config.getDoubleFromExpression("amount", data)
        val requireBalance = config.getOrElse("require_balance", true) { getBool(it) }

        if (requireBalance && !user.has(amount)) {
            return false
        }

        user.withdraw(amount)

        return true
    }
}
