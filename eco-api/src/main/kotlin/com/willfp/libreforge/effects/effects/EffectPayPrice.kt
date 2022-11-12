package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.price.Prices
import com.willfp.libreforge.ConfigViolation
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
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val price = Prices.create(
            config.getString("value"),
            config.getString("type"),
            config.toMathContext(data)
        )

        price.pay(player)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("value")) violations.add(
            ConfigViolation(
                "value",
                "You must specify the value of the price to pay!"
            )
        )

        if (!config.has("type")) violations.add(
            ConfigViolation(
                "type",
                "You must specify the type of price (coins, xpl, etc)!"
            )
        )

        return violations
    }
}
