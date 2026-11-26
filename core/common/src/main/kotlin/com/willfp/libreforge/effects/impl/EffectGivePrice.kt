package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.price.Prices
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.earning.EarningLimitGroup
import com.willfp.libreforge.earning.EarningLimits
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGivePrice : Effect<EarningLimitGroup?>("give_price") {
    override val description = "Gives the player a reward using the eco Price system, supporting any registered price type."
    override val categories = setOf("economy")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "value",
            "You must specify the value of the price to give!",
            description = "The amount to give, as an expression string (may reference placeholders).",
            type = ArgType.EXPRESSION,
            example = "%level% * 100"
        )
        require(
            "type",
            "You must specify the value of the price (coins, xpl, etc.)!",
            description = "The eco price type identifier (e.g. coins, xp, points:mytype).",
            type = ArgType.STRING,
            example = "points:mytype"
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

        val value = config.getFormattedString("value", data)
        val type = config.getFormattedString("type", data)
        val context = config.toPlaceholderContext(data)

        var price = Prices.create(
            value,
            type,
            context
        )

        if (compileData != null) {
            val amount = EarningLimits.addEarned(player, compileData, price.getValue(player)) {
                price = Prices.create(
                    it.toString(),
                    type,
                    context
                )
                price.getValue(player)
            }
            if (amount == 0.0) return true
        }

        price.giveTo(player)

        return true
    }
}
