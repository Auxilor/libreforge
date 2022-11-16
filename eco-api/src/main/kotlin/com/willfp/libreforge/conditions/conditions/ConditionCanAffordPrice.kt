package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.math.MathContext
import com.willfp.eco.core.price.Prices
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionCanAffordPrice : Condition("can_afford_price") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        val price = Prices.create(
            config.getString("value"),
            config.getString("type"),
            MathContext.of(config)
        )

        return price.canAfford(player)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("value")) violations.add(
            ConfigViolation(
                "value",
                "You must specify the value of the price!"
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
