package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.math.MathContext
import com.willfp.eco.core.price.Prices
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionCanAffordPrice : Condition("can_afford_price") {
    override val arguments = arguments {
        require("value", "You must specify the value of the price!")
        require("type", "You must specify the type of price (coins, xpl, etc.)!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        val price = Prices.create(
            config.getString("value"),
            config.getString("type"),
            MathContext.of(config)
        )

        return price.canAfford(player)
    }
}
