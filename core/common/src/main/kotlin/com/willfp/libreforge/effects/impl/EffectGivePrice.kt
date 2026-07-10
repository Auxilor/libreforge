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

object EffectGivePrice : Effect<NoCompileData>("give_price") {
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
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        Prices.create(
            config.getString("value"),
            config.getString("type"),
            config.toPlaceholderContext(data)
        ).giveTo(player)

        return true
    }
}
