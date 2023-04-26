package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.Prices
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.toPlaceholderContext
import org.bukkit.entity.Player

object ConditionCanAffordPrice : Condition<Price>("can_afford_price") {
    override val arguments = arguments {
        require("value", "You must specify the value of the price!")
        require("type", "You must specify the type of price (coins, xpl, etc.)!")
    }

    override fun isMet(player: Player, config: Config, compileData: Price): Boolean {
        return compileData.canAfford(player)
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Price {
        return Prices.create(
            config.getString("value"),
            config.getString("type"),
            config.toPlaceholderContext()
        )
    }
}
