package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.price.Prices
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.toMathContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectPayPrice : Effect<NoCompileData>("pay_price") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("value", "You must specify the value of the price to pay!")
        require("type", "You must specify the type of price (coins, xpl, etc.)!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        Prices.create(
            config.getString("value"),
            config.getString("type"),
            config.toMathContext(data)
        ).pay(player)

        return true
    }
}
