package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.Prices
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionCanAffordPrice : Condition<Price>("can_afford_price") {
    override val arguments = arguments {
        require("value", "You must specify the value of the price!")
        require("type", "You must specify the type of price (coins, xpl, etc.)!")
    }

    override fun isMet(dispatcher: Dispatcher<*>, config: Config, holder: ProvidedHolder, compileData: Price): Boolean {
        val player = dispatcher.get<Player>() ?: return false

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
