package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.price.Prices
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectPayPrice : Effect<NoCompileData>("pay_price") {
    override val description = "Deducts a price of the specified type from the player."
    override val categories = setOf("economy")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "value",
            "You must specify the value of the price to pay!",
            description = "The amount to deduct from the player. Supports expressions and placeholders.",
            type = ArgType.STRING,
            example = "%level% * 10"
        )
        require(
            "type",
            "You must specify the type of price (coins, xpl, etc.)!",
            description = "The price type identifier (e.g. coins, xp, xpl, item).",
            type = ArgType.STRING,
            example = "coins"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        Prices.create(
            config.getString("value"),
            config.getString("type"),
            config.toPlaceholderContext(data)
        ).pay(player)

        return true
    }
}
