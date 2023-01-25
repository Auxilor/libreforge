package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.price.Prices
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.toMathContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectPayPrice : Effect(
    "pay_price",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("value", "You must specify the value of the price to pay!")
        require("type", "You must specify the type of price (coins, xpl, etc.)!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val price = Prices.create(
            config.getString("value"),
            config.getString("type"),
            config.toMathContext(data)
        )

        price.pay(player)
    }
}
